package com.ticket12306.android.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ticket12306.android.R
import com.ticket12306.android.databinding.FragmentBookingBinding
import com.ticket12306.android.util.EmptyStateHelper
import com.ticket12306.android.util.SkeletonHelper
import com.ticket12306.android.util.ViewStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by viewModels()
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var viewStateManager: ViewStateManager
    private var isFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        initViewState()
        observeState()
    }

    /** 初始化抢票任务列表RecyclerView */
    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(
            onStartClick = { task ->
                viewModel.startBooking(task)
            },
            onStopClick = { task ->
                viewModel.stopBooking(task.id)
            },
            onDeleteClick = { task ->
                viewModel.deleteTask(task.id)
            },
            onConfigClick = { task ->
                val action = BookingFragmentDirections
                    .actionNavigationBookingToBookingConfigFragment(null, task)
                findNavController().navigate(action)
            },
            getStatusText = { taskId ->
                viewModel.getStatusText(taskId)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingAdapter
        }
    }

    /**
     * 设置下拉刷新
     * 下拉刷新时重新加载抢票任务列表
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshTasks()
            binding.swipeRefresh.isRefreshing = false
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
     * 观察ViewModel状态变化，更新UI
     * 步骤：
     * 1. 监听任务列表，更新适配器和视图状态
     * 2. 监听加载状态，控制骨架屏
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookingTasks.collect { tasks ->
                    bookingAdapter.submitList(tasks)

                    if (isFirstLoad) {
                        isFirstLoad = false
                    }

                    viewStateManager.showContentOrEmpty(
                        isEmpty = tasks.isEmpty(),
                        iconRes = android.R.drawable.ic_menu_add,
                        title = getString(R.string.empty_booking_title),
                        subtitle = getString(R.string.empty_booking_subtitle),
                        actionText = getString(R.string.empty_action_search)
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading && isFirstLoad) {
                        viewStateManager.showLoading()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
