package com.ticket12306.android.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ticket12306.android.R
import com.ticket12306.android.databinding.FragmentOrderBinding
import com.ticket12306.android.util.EmptyStateHelper
import com.ticket12306.android.util.SkeletonHelper
import com.ticket12306.android.util.ViewStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var viewStateManager: ViewStateManager
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabLayout()
        setupSwipeRefresh()
        initViewState()
        observeState()
    }

    /**
     * 初始化订单列表RecyclerView
     * 步骤：
     * 1. 创建OrderAdapter，设置点击事件跳转订单详情
     * 2. 配置LinearLayoutManager
     * 3. 绑定Adapter
     */
    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter { order ->
            val action = OrderFragmentDirections
                .actionNavigationOrderToOrderDetailFragment(order)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = orderAdapter
        }
    }

    /**
     * 初始化订单状态筛选TabLayout
     * 步骤：
     * 1. 添加全部/待支付/已支付/已取消四个Tab
     * 2. 监听Tab选择事件，更新ViewModel筛选条件
     */
    private fun setupTabLayout() {
        OrderStatusFilter.values().forEach { filter ->
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(filter.label))
        }

        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                val filter = OrderStatusFilter.values()[tab.position]
                viewModel.setFilter(filter)
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
        })
    }

    /**
     * 设置下拉刷新
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshOrders()
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
        val skeletonHelper = SkeletonHelper(binding.skeletonBookingList.shimmerContainer)
        val emptyStateHelper = EmptyStateHelper(binding.layoutEmptyState.root)
        viewStateManager = ViewStateManager(binding.recyclerView, skeletonHelper, emptyStateHelper)
        viewStateManager.showLoading()
    }

    /**
     * 观察ViewModel状态变化
     * 步骤：分别观察订单列表、加载状态、消息
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    orderAdapter.submitList(state.filteredOrders)
                    binding.swipeRefresh.isRefreshing = state.isRefreshing

                    if (state.isLoading && isFirstLoad) {
                        viewStateManager.showLoading()
                    } else {
                        if (isFirstLoad) isFirstLoad = false

                        viewStateManager.showContentOrEmpty(
                            isEmpty = state.filteredOrders.isEmpty(),
                            iconRes = android.R.drawable.ic_menu_agenda,
                            title = getString(R.string.empty_order_title),
                            subtitle = getString(R.string.empty_order_subtitle),
                            actionText = getString(R.string.empty_action_refresh),
                            onActionClick = { viewModel.refreshOrders() }
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.successMessage.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
