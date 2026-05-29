package com.ticket12306.android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ticket12306.android.R
import com.ticket12306.android.databinding.FragmentTicketBinding
import com.ticket12306.android.ui.ticket.QueryState
import com.ticket12306.android.ui.ticket.SeatInfoAdapter
import com.ticket12306.android.ui.ticket.SortField
import com.ticket12306.android.ui.ticket.TimePeriod
import com.ticket12306.android.ui.ticket.TicketAdapter
import com.ticket12306.android.ui.ticket.TicketViewModel
import com.ticket12306.android.ui.ticket.TrainType
import com.ticket12306.android.util.DateUtils
import com.ticket12306.android.util.EmptyStateHelper
import com.ticket12306.android.util.SkeletonHelper
import com.ticket12306.android.util.ViewStateManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TicketFragment : Fragment() {

    private var _binding: FragmentTicketBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketViewModel by activityViewModels()
    private lateinit var ticketAdapter: TicketAdapter
    private lateinit var viewStateManager: ViewStateManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchInputs()
        setupDateNavigation()
        setupDateTagChips()
        setupTrainTypeChips()
        setupSortButtons()
        setupFilterButton()
        setupSwapButton()
        setupSwipeRefresh()
        setupFragmentResultListener()
        initViewState()
        observeState()
        initDefaultDate()
    }

    // ==================== RecyclerView初始化 ====================

    /** 初始化车次列表RecyclerView */
    private fun setupRecyclerView() {
        ticketAdapter = TicketAdapter { ticket ->
            viewModel.selectTicket(ticket)
            findNavController().navigate(
                TicketFragmentDirections.actionNavigationTicketToTicketDetailFragment()
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ticketAdapter
        }
    }

    // ==================== 视图状态管理 ====================

    /**
     * 初始化视图状态管理器
     * 步骤：
     * 1. 创建骨架屏控制器
     * 2. 创建空状态控制器
     * 3. 创建视图状态管理器，绑定三者关系
     */
    private fun initViewState() {
        val skeletonHelper = SkeletonHelper(binding.skeletonTicketList.shimmerContainer)
        val emptyStateHelper = EmptyStateHelper(binding.layoutEmptyState.root)
        viewStateManager = ViewStateManager(binding.recyclerView, skeletonHelper, emptyStateHelper)
    }

    // ==================== 下拉刷新 ====================

    /**
     * 设置下拉刷新
     * 下拉刷新时重新执行车票查询
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary, R.color.accent)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.queryTickets()
        }
    }

    // ==================== 搜索输入 ====================

    /** 设置出发站、到达站、日期输入框的点击事件 */
    private fun setupSearchInputs() {
        binding.etFromStation.setOnClickListener {
            findNavController().navigate(
                TicketFragmentDirections.actionNavigationTicketToStationSelectFragment(true)
            )
        }

        binding.etToStation.setOnClickListener {
            findNavController().navigate(
                TicketFragmentDirections.actionNavigationTicketToStationSelectFragment(false)
            )
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSearch.setOnClickListener {
            viewModel.queryTickets()
        }
    }

    // ==================== 日期导航 ====================

    /** 设置前一天/后一天导航按钮 */
    private fun setupDateNavigation() {
        binding.btnPreviousDay.setOnClickListener {
            viewModel.navigateToPreviousDay()
        }

        binding.btnNextDay.setOnClickListener {
            viewModel.navigateToNextDay()
        }
    }

    // ==================== 日期快捷标签 ====================

    /** 设置今天/明天/后天快捷选择标签 */
    private fun setupDateTagChips() {
        binding.chipToday.setOnClickListener {
            viewModel.setSelectedDate(DateUtils.getToday())
        }

        binding.chipTomorrow.setOnClickListener {
            viewModel.setSelectedDate(DateUtils.getTomorrow())
        }

        binding.chipDayAfterTomorrow.setOnClickListener {
            viewModel.setSelectedDate(DateUtils.getDateAfterDays(2))
        }
    }

    // ==================== 日期选择器 ====================

    /**
     * 显示MaterialDatePicker日期选择器
     * 步骤：
     * 1. 构建日期约束（今天到预售期30天内）
     * 2. 创建MaterialDatePicker实例
     * 3. 设置选中回调
     * 4. 显示选择器
     */
    private fun showDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setStart(DateUtils.getTodayStartMillis())
            .setEnd(DateUtils.getMaxBookingDateMillis())
            .setValidator(DateValidatorPointForward.now())
            .build()

        val currentSelection = DateUtils.parseDate(viewModel.selectedDate.value)
        val selectionMillis = currentSelection?.time ?: DateUtils.getTodayStartMillis()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(selectionMillis)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = DateUtils.formatDate(java.util.Date(selection))
            viewModel.setSelectedDate(selectedDate)
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    // ==================== 车次类型筛选 ====================

    /** 设置车次类型筛选ChipGroup的监听 */
    private fun setupTrainTypeChips() {
        binding.chipGroupTrainType.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val trainType = when (checkedIds.first()) {
                R.id.chip_all -> TrainType.ALL
                R.id.chip_high_speed -> TrainType.HIGH_SPEED
                R.id.chip_electric -> TrainType.ELECTRIC
                R.id.chip_direct -> TrainType.DIRECT
                R.id.chip_express -> TrainType.EXPRESS
                R.id.chip_fast -> TrainType.FAST
                R.id.chip_other -> TrainType.OTHER
                else -> TrainType.ALL
            }
            viewModel.setTrainTypeFilter(trainType)
        }
    }

    // ==================== 排序按钮 ====================

    /** 设置排序按钮的点击事件，点击同一字段切换升降序 */
    private fun setupSortButtons() {
        binding.btnSortDeparture.setOnClickListener {
            viewModel.setSortField(SortField.DEPARTURE_TIME)
        }

        binding.btnSortArrival.setOnClickListener {
            viewModel.setSortField(SortField.ARRIVAL_TIME)
        }

        binding.btnSortDuration.setOnClickListener {
            viewModel.setSortField(SortField.DURATION)
        }
    }

    // ==================== 筛选弹窗 ====================

    /**
     * 显示高级筛选对话框
     * 步骤：
     * 1. 构建筛选对话框布局（时间段Chips + 仅看有票开关）
     * 2. 初始化当前筛选状态
     * 3. 确定后应用筛选条件
     */
    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
    }

    /** 显示筛选对话框 */
    private fun showFilterDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_ticket_filter, null)

        val chipMorning = dialogView.findViewById<Chip>(R.id.chip_time_morning)
        val chipForenoon = dialogView.findViewById<Chip>(R.id.chip_time_forenoon)
        val chipAfternoon = dialogView.findViewById<Chip>(R.id.chip_time_afternoon)
        val chipEvening = dialogView.findViewById<Chip>(R.id.chip_time_evening)
        val switchOnlyTicket = dialogView.findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(
            R.id.switch_only_ticket
        )

        val currentTimePeriod = viewModel.selectedTimePeriod.value
        when (currentTimePeriod) {
            TimePeriod.MORNING -> chipMorning.isChecked = true
            TimePeriod.FORENOON -> chipForenoon.isChecked = true
            TimePeriod.AFTERNOON -> chipAfternoon.isChecked = true
            TimePeriod.EVENING -> chipEvening.isChecked = true
            null -> {}
        }

        switchOnlyTicket.isChecked = viewModel.onlyHasTicket.value

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.filter_title))
            .setView(dialogView)
            .setNegativeButton(getString(R.string.filter_reset)) { _, _ ->
                viewModel.resetFilters()
            }
            .setPositiveButton(getString(R.string.filter_confirm)) { _, _ ->
                val timePeriod = when {
                    chipMorning.isChecked -> TimePeriod.MORNING
                    chipForenoon.isChecked -> TimePeriod.FORENOON
                    chipAfternoon.isChecked -> TimePeriod.AFTERNOON
                    chipEvening.isChecked -> TimePeriod.EVENING
                    else -> null
                }
                viewModel.setTimePeriodFilter(timePeriod)
                viewModel.setOnlyHasTicket(switchOnlyTicket.isChecked)
            }
            .show()
    }

    // ==================== 交换按钮 ====================

    /** 设置出发站和到达站交换按钮 */
    private fun setupSwapButton() {
        binding.btnSwap.setOnClickListener {
            viewModel.swapStations()
        }
    }

    // ==================== FragmentResult监听 ====================

    /** 监听车站选择Fragment的返回结果 */
    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("station_select", viewLifecycleOwner) { _, bundle ->
            val stationCode = bundle.getString("station_code") ?: ""
            val stationName = bundle.getString("station_name") ?: ""
            val isDeparture = bundle.getBoolean("is_departure", true)

            if (isDeparture) {
                viewModel.setFromStation(stationCode, stationName)
            } else {
                viewModel.setToStation(stationCode, stationName)
            }
        }
    }

    // ==================== 状态观察 ====================

    /** 观察ViewModel状态变化，更新UI */
    private fun observeState() {
        observeDisplayTickets()
        observeQueryState()
        observeStationState()
        observeDateState()
        observeSortState()
        observeResultCount()
        observeMessages()
    }

    /**
     * 观察展示车次列表，更新RecyclerView和空状态
     * 步骤：
     * 1. 列表为空且查询成功 -> 显示无结果空状态
     * 2. 列表为空且初始状态 -> 显示查询提示空状态
     * 3. 列表不为空 -> 显示内容
     */
    private fun observeDisplayTickets() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.displayTickets.collect { tickets ->
                    ticketAdapter.submitList(tickets)
                    updateEmptyState(tickets.isEmpty())
                }
            }
        }
    }

    /**
     * 观察查询状态，控制骨架屏和下拉刷新显示
     * 步骤：
     * 1. Loading -> 显示骨架屏，停止下拉刷新
     * 2. Success -> 隐藏骨架屏，停止下拉刷新
     * 3. Error -> 隐藏骨架屏，停止下拉刷新
     * 4. Idle -> 隐藏骨架屏，停止下拉刷新
     */
    private fun observeQueryState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.queryState.collect { state ->
                    when (state) {
                        is QueryState.Loading -> {
                            viewStateManager.showLoading()
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is QueryState.Success -> {
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is QueryState.Error -> {
                            viewStateManager.showEmpty(
                                iconRes = android.R.drawable.ic_dialog_alert,
                                title = getString(R.string.empty_ticket_title),
                                subtitle = state.message,
                                actionText = getString(R.string.empty_action_retry),
                                onActionClick = { viewModel.queryTickets() }
                            )
                            binding.swipeRefresh.isRefreshing = false
                        }
                        is QueryState.Idle -> {
                            binding.swipeRefresh.isRefreshing = false
                        }
                    }
                }
            }
        }
    }

    /** 观察出发站和到达站状态，更新输入框 */
    private fun observeStationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fromStationName.collect { name ->
                    binding.etFromStation.setText(name)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toStationName.collect { name ->
                    binding.etToStation.setText(name)
                }
            }
        }
    }

    /** 观察日期状态，更新日期显示和导航按钮状态 */
    private fun observeDateState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedDate.collect { date ->
                    val displayText = DateUtils.getDateDisplayText(date)
                    binding.etDate.setText(displayText)

                    binding.btnPreviousDay.isEnabled = viewModel.canNavigateToPreviousDay()
                    binding.btnNextDay.isEnabled = viewModel.canNavigateToNextDay()
                    binding.btnPreviousDay.alpha = if (viewModel.canNavigateToPreviousDay()) 1.0f else 0.4f
                    binding.btnNextDay.alpha = if (viewModel.canNavigateToNextDay()) 1.0f else 0.4f

                    updateDateTagSelection(date)
                }
            }
        }
    }

    /** 观察排序状态，高亮当前排序按钮 */
    private fun observeSortState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.sortField, viewModel.sortOrder) { field, order ->
                    updateSortButtonHighlight(field, order)
                }.collect {}
            }
        }
    }

    /** 观察结果数量，更新数量文字 */
    private fun observeResultCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.totalResultCount.collect { count ->
                    val filteredCount = viewModel.displayTickets.value.size
                    if (count > 0) {
                        binding.tvResultCount.text = if (filteredCount == count) {
                            "共${count}趟车次"
                        } else {
                            "筛选出${filteredCount}/${count}趟"
                        }
                    } else {
                        binding.tvResultCount.text = ""
                    }
                }
            }
        }
    }

    /** 观察错误和成功消息 */
    private fun observeMessages() {
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

    // ==================== UI辅助方法 ====================

    /** 初始化默认日期为今天 */
    private fun initDefaultDate() {
        val today = DateUtils.getToday()
        viewModel.setSelectedDate(today)
    }

    /**
     * 更新日期快捷标签的选中状态
     * 步骤：判断当前日期是否为今天/明天/后天，选中对应标签
     */
    private fun updateDateTagSelection(date: String) {
        binding.chipToday.isChecked = DateUtils.isToday(date)
        binding.chipTomorrow.isChecked = DateUtils.isTomorrow(date)
        binding.chipDayAfterTomorrow.isChecked = date == DateUtils.getDateAfterDays(2)
    }

    /**
     * 更新空状态视图
     * 步骤：根据查询状态和列表是否为空决定显示内容
     */
    private fun updateEmptyState(isEmpty: Boolean) {
        val queryState = viewModel.queryState.value
        if (isEmpty && queryState is QueryState.Success) {
            viewStateManager.showEmpty(
                iconRes = android.R.drawable.ic_menu_search,
                title = getString(R.string.empty_ticket_title),
                subtitle = getString(R.string.empty_ticket_subtitle),
                actionText = getString(R.string.empty_action_search),
                onActionClick = { binding.btnSearch.performClick() }
            )
        } else if (isEmpty && queryState is QueryState.Idle) {
            viewStateManager.showEmpty(
                iconRes = android.R.drawable.ic_menu_search,
                title = getString(R.string.empty_ticket_title),
                subtitle = getString(R.string.empty_query_hint)
            )
        } else if (!isEmpty) {
            viewStateManager.showContent()
        }
    }

    /**
     * 更新排序按钮的高亮状态
     * 步骤：当前排序字段高亮为主色，其余为次要文字色
     */
    private fun updateSortButtonHighlight(field: SortField, order: com.ticket12306.android.ui.ticket.SortOrder) {
        val primaryColor = resources.getColor(R.color.primary, null)
        val secondaryColor = resources.getColor(R.color.secondary_text, null)

        val arrowSuffix = if (order == com.ticket12306.android.ui.ticket.SortOrder.ASCENDING) " ↑" else " ↓"

        binding.btnSortDeparture.apply {
            setTextColor(if (field == SortField.DEPARTURE_TIME) primaryColor else secondaryColor)
            text = getString(R.string.sort_departure_time) + if (field == SortField.DEPARTURE_TIME) arrowSuffix else ""
        }

        binding.btnSortArrival.apply {
            setTextColor(if (field == SortField.ARRIVAL_TIME) primaryColor else secondaryColor)
            text = getString(R.string.sort_arrival_time) + if (field == SortField.ARRIVAL_TIME) arrowSuffix else ""
        }

        binding.btnSortDuration.apply {
            setTextColor(if (field == SortField.DURATION) primaryColor else secondaryColor)
            text = getString(R.string.sort_duration) + if (field == SortField.DURATION) arrowSuffix else ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
