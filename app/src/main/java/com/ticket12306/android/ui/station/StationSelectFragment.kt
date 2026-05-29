package com.ticket12306.android.ui.station

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.ticket12306.android.R
import com.ticket12306.android.data.model.Station
import com.ticket12306.android.data.repository.StationRepository
import com.ticket12306.android.databinding.FragmentStationSelectBinding
import com.ticket12306.android.util.EmptyStateHelper
import com.ticket12306.android.util.SkeletonHelper
import com.ticket12306.android.util.ViewStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StationSelectFragment : Fragment() {

    private var _binding: FragmentStationSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StationViewModel by viewModels()
    private val args: StationSelectFragmentArgs by navArgs()
    private lateinit var stationAdapter: StationAdapter
    private lateinit var viewStateManager: ViewStateManager
    private var isFirstLoad = true

    /** 当前选择模式：true=出发站，false=到达站 */
    private var isDeparture: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isDeparture = args.isDeparture

        setupTabLayout()
        setupRecyclerView()
        setupSearchInput()
        setupHotStations()
        initViewState()
        observeState()
    }

    /**
     * 初始化出发站/到达站切换Tab
     * 步骤：
     * 1. 添加出发站和到达站两个Tab
     * 2. 根据导航参数选中对应Tab
     * 3. 监听Tab切换事件，更新选择模式
     */
    private fun setupTabLayout() {
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(R.string.select_departure)
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText(R.string.select_arrival)
        )

        binding.tabLayout.selectTab(
            binding.tabLayout.getTabAt(if (isDeparture) 0 else 1)
        )

        binding.tabLayout.addOnTabSelectedListener(
            object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                    isDeparture = tab.position == 0
                }

                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}

                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            }
        )
    }

    /**
     * 初始化车站列表RecyclerView
     * 点击车站项后通过FragmentResult返回选择结果
     */
    private fun setupRecyclerView() {
        stationAdapter = StationAdapter { station ->
            onStationSelected(station)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stationAdapter
        }
    }

    /**
     * 初始化搜索输入框
     * 监听文本变化触发搜索，支持键盘搜索按钮
     */
    private fun setupSearchInput() {
        binding.etSearch.addTextChangedListener { text ->
            val query = text?.toString() ?: ""
            viewModel.searchStations(query)
            updateHotStationsVisibility(query)
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchStations(binding.etSearch.text?.toString() ?: "")
                true
            } else {
                false
            }
        }
    }

    /**
     * 初始化热门车站Chips
     * 步骤：
     * 1. 观察ViewModel中的热门车站列表
     * 2. 动态创建Chip并添加到ChipGroup
     * 3. 每个Chip点击后直接选择对应车站
     */
    private fun setupHotStations() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hotStations.collect { hotList ->
                    binding.chipGroupHot.removeAllViews()

                    if (hotList.isEmpty()) {
                        setupFallbackHotStations()
                        return@collect
                    }

                    for (station in hotList) {
                        val chip = createStationChip(station.name) {
                            onStationSelected(station)
                        }
                        binding.chipGroupHot.addView(chip)
                    }
                }
            }
        }
    }

    /**
     * 初始化视图状态管理器
     * 步骤：
     * 1. 创建骨架屏控制器
     * 2. 创建空状态控制器
     * 3. 创建视图状态管理器
     * 4. 首次加载显示骨架屏
     */
    private fun initViewState() {
        val skeletonHelper = SkeletonHelper(binding.skeletonStationList.shimmerContainer)
        val emptyStateHelper = EmptyStateHelper(binding.layoutEmptyState.root)
        viewStateManager = ViewStateManager(binding.recyclerView, skeletonHelper, emptyStateHelper)
        viewStateManager.showLoading()
    }

    /**
     * 降级方案：当本地数据库无缓存时，使用预定义的热门车站名称展示
     */
    private fun setupFallbackHotStations() {
        for ((code, name) in StationRepository.HOT_STATION_NAMES) {
            val chip = createStationChip(name) {
                val station = Station(
                    code = code,
                    name = name,
                    pinyin = "",
                    pinyinInitial = ""
                )
                onStationSelected(station)
            }
            binding.chipGroupHot.addView(chip)
        }
    }

    /**
     * 创建热门车站Chip视图
     * @param name 车站名称
     * @param onClick 点击回调
     */
    private fun createStationChip(name: String, onClick: () -> Unit): Chip {
        return Chip(requireContext()).apply {
            text = name
            isClickable = true
            isCheckable = false
            setChipBackgroundColorResource(R.color.hot_station_bg)
            setTextColor(resources.getColor(R.color.hot_station_text, null))
            textSize = 13f
            setOnClickListener { onClick() }
        }
    }

    /**
     * 根据搜索关键字控制热门车站区域的可见性
     * 有搜索内容时隐藏热门车站，无搜索内容时显示
     */
    private fun updateHotStationsVisibility(query: String) {
        if (query.isBlank()) {
            binding.layoutHotStations.visibility = View.VISIBLE
            binding.divider.visibility = View.VISIBLE
        } else {
            binding.layoutHotStations.visibility = View.GONE
            binding.divider.visibility = View.GONE
        }
    }

    /**
     * 观察ViewModel状态变化，更新UI
     * 监听：搜索结果列表、搜索关键字（高亮）、加载状态
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stations.collect { stations ->
                    stationAdapter.submitList(stations)
                    stationAdapter.setSearchQuery(viewModel.searchQuery.value)

                    if (isFirstLoad && stations.isNotEmpty()) {
                        isFirstLoad = false
                    }

                    val query = viewModel.searchQuery.value
                    viewStateManager.showContentOrEmpty(
                        isEmpty = stations.isEmpty() && query.isNotBlank(),
                        iconRes = android.R.drawable.ic_menu_search,
                        title = getString(R.string.empty_station_search_title),
                        subtitle = getString(R.string.empty_station_search_subtitle),
                        actionText = getString(R.string.empty_action_retry),
                        onActionClick = { binding.etSearch.text?.clear() }
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchQuery.collect { query ->
                    stationAdapter.setSearchQuery(query)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingStations.collect { isLoading ->
                    if (isLoading && isFirstLoad) {
                        viewStateManager.showLoading()
                    } else if (!isLoading && isFirstLoad) {
                        isFirstLoad = false
                        val stations = viewModel.stations.value
                        val query = viewModel.searchQuery.value
                        viewStateManager.showContentOrEmpty(
                            isEmpty = stations.isEmpty() && query.isNotBlank(),
                            iconRes = android.R.drawable.ic_menu_search,
                            title = getString(R.string.empty_station_search_title),
                            subtitle = getString(R.string.empty_station_search_subtitle),
                            actionText = getString(R.string.empty_action_retry),
                            onActionClick = { binding.etSearch.text?.clear() }
                        )
                    }
                }
            }
        }
    }

    /**
     * 处理车站选择结果
     * 步骤：
     * 1. 将选中车站信息打包到Bundle
     * 2. 通过FragmentResult通知上一级页面
     * 3. 返回上一页
     */
    private fun onStationSelected(station: Station) {
        val result = Bundle().apply {
            putString("station_code", station.code)
            putString("station_name", station.name)
            putBoolean("is_departure", isDeparture)
        }
        parentFragmentManager.setFragmentResult("station_select", result)
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
