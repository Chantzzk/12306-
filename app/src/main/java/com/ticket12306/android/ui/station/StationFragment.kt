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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ticket12306.android.R
import com.ticket12306.android.data.model.Station
import com.ticket12306.android.databinding.FragmentStationBinding
import com.ticket12306.android.util.EmptyStateHelper
import com.ticket12306.android.util.SkeletonHelper
import com.ticket12306.android.util.ViewStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StationFragment : Fragment() {

    private var _binding: FragmentStationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StationViewModel by viewModels()
    private lateinit var stationAdapter: StationAdapter
    private lateinit var viewStateManager: ViewStateManager
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViews()
        initViewState()
        observeState()
    }

    /**
     * 初始化车站列表RecyclerView
     * 点击车站项可查看详情（当前仅打印日志）
     */
    private fun setupRecyclerView() {
        stationAdapter = StationAdapter { station ->
            onStationClicked(station)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = stationAdapter
        }
    }

    /**
     * 初始化视图交互
     * 搜索框文本变化触发搜索（带防抖）
     * 下拉刷新触发网络数据更新
     */
    private fun setupViews() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.searchStations(text?.toString() ?: "")
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchStations(binding.etSearch.text?.toString() ?: "")
                true
            } else {
                false
            }
        }

        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchStations()
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
     * 观察ViewModel状态变化，更新UI
     * 监听：车站列表、搜索关键字（高亮）、加载状态
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
                        isEmpty = stations.isEmpty(),
                        iconRes = if (query.isNotBlank()) android.R.drawable.ic_menu_search else android.R.drawable.ic_menu_myplaces,
                        title = if (query.isNotBlank()) getString(R.string.empty_station_search_title) else getString(R.string.empty_station_title),
                        subtitle = if (query.isNotBlank()) getString(R.string.empty_station_search_subtitle) else getString(R.string.empty_station_subtitle),
                        actionText = getString(R.string.empty_action_refresh),
                        onActionClick = { viewModel.fetchStations() }
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
                    binding.swipeRefresh.isRefreshing = isLoading
                    if (isLoading && isFirstLoad) {
                        viewStateManager.showLoading()
                    }
                }
            }
        }
    }

    /**
     * 处理车站项点击事件
     */
    private fun onStationClicked(station: Station) {
        // 后续可扩展：导航到车站详情页
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
