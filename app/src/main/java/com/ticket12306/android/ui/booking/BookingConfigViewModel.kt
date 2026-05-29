package com.ticket12306.android.ui.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ticket12306.android.booking.BookingStateManager
import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.BookingStrategyType
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.LogType
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingConfigViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ticketRepository: TicketRepository,
    private val bookingStateManager: BookingStateManager,
    private val database: AppDatabase
) : BaseViewModel() {

    private val _ticketInfo = MutableStateFlow<TicketInfo?>(null)
    val ticketInfo: StateFlow<TicketInfo?> = _ticketInfo.asStateFlow()

    private val _bookingTask = MutableStateFlow<BookingTask?>(null)
    val bookingTask: StateFlow<BookingTask?> = _bookingTask.asStateFlow()

    private val _selectedStrategy = MutableStateFlow(BookingStrategyType.NORMAL)
    val selectedStrategy: StateFlow<BookingStrategyType> = _selectedStrategy.asStateFlow()

    private val _refreshInterval = MutableStateFlow(5)
    val refreshInterval: StateFlow<Int> = _refreshInterval.asStateFlow()

    private val _maxRetryCount = MutableStateFlow(50)
    val maxRetryCount: StateFlow<Int> = _maxRetryCount.asStateFlow()

    private val _seatList = MutableStateFlow<List<SeatInfo>>(emptyList())
    val seatList: StateFlow<List<SeatInfo>> = _seatList.asStateFlow()

    private val _selectedSeatTypes = MutableStateFlow<Set<String>>(emptySet())
    val selectedSeatTypes: StateFlow<Set<String>> = _selectedSeatTypes.asStateFlow()

    private val _passengers = MutableStateFlow<List<Passenger>>(emptyList())
    val passengers: StateFlow<List<Passenger>> = _passengers.asStateFlow()

    private val _selectedPassengerCodes = MutableStateFlow<Set<String>>(emptySet())
    val selectedPassengerCodes: StateFlow<Set<String>> = _selectedPassengerCodes.asStateFlow()

    private val _acceptWaitlist = MutableStateFlow(false)
    val acceptWaitlist: StateFlow<Boolean> = _acceptWaitlist.asStateFlow()

    private val _currentStatus = MutableStateFlow(BookingStatus.PENDING)
    val currentStatus: StateFlow<BookingStatus> = _currentStatus.asStateFlow()

    private val _logs = MutableStateFlow<List<BookingLog>>(emptyList())
    val logs: StateFlow<List<BookingLog>> = _logs.asStateFlow()

    init {
        val ticket = savedStateHandle.get<TicketInfo>("ticketInfo")
        val task = savedStateHandle.get<BookingTask>("bookingTask")

        if (task != null) {
            loadExistingTask(task)
        } else if (ticket != null) {
            initFromTicketInfo(ticket)
        }

        loadPassengers()
    }

    /**
     * 从已有任务加载配置
     * 步骤：
     * 1. 设置任务信息
     * 2. 恢复策略配置
     * 3. 恢复刷新间隔
     * 4. 恢复重试次数
     * 5. 恢复座次偏好
     * 6. 恢复候补设置
     * 7. 恢复乘客选择
     * 8. 加载任务状态和日志
     */
    private fun loadExistingTask(task: BookingTask) {
        _bookingTask.value = task
        _selectedStrategy.value = try {
            BookingStrategyType.valueOf(task.strategy)
        } catch (e: IllegalArgumentException) {
            BookingStrategyType.NORMAL
        }
        _refreshInterval.value = task.refreshInterval
        _maxRetryCount.value = task.maxRetryCount
        _selectedSeatTypes.value = task.seatPreferences.toSet() + task.seatType
        _acceptWaitlist.value = task.acceptWaitlist
        _selectedPassengerCodes.value = task.passengerIds.toSet()

        viewModelScope.launch {
            val status = bookingStateManager.getTaskStatus(task.id)
            _currentStatus.value = status
        }

        loadTaskLogs(task.id)
    }

    /**
     * 从车次信息初始化配置
     * 步骤：
     * 1. 设置车次信息
     * 2. 初始化座次列表
     * 3. 默认选中第一个座次
     */
    private fun initFromTicketInfo(ticket: TicketInfo) {
        _ticketInfo.value = ticket
        val seats = ticket.seatTypes.values.toList()
        _seatList.value = seats
        if (seats.isNotEmpty()) {
            _selectedSeatTypes.value = setOf(seats.first().seatType)
        }
    }

    /**
     * 加载乘客列表
     * 步骤：
     * 1. 从本地数据库监听
     * 2. 尝试从网络刷新
     */
    private fun loadPassengers() {
        viewModelScope.launch {
            database.passengerDao().getAllPassengers().collect { passengerList ->
                if (passengerList.isNotEmpty()) {
                    _passengers.value = passengerList
                }
            }
        }
    }

    /**
     * 加载任务日志
     * 步骤：从数据库查询指定任务的最近日志
     */
    private fun loadTaskLogs(taskId: Long) {
        viewModelScope.launch {
            database.bookingLogDao().getLogsByTaskId(taskId).collect { logList ->
                _logs.value = logList
            }
        }
    }

    /** 设置抢票策略 */
    fun setStrategy(strategy: BookingStrategyType) {
        _selectedStrategy.value = strategy
    }

    /** 设置刷新间隔（秒） */
    fun setRefreshInterval(seconds: Int) {
        _refreshInterval.value = seconds
    }

    /** 设置最大重试次数 */
    fun setMaxRetryCount(count: Int) {
        _maxRetryCount.value = count
    }

    /** 切换座次选中状态（多选） */
    fun toggleSeatType(seatType: String) {
        val current = _selectedSeatTypes.value.toMutableSet()
        if (current.contains(seatType)) {
            if (current.size > 1) {
                current.remove(seatType)
            }
        } else {
            current.add(seatType)
        }
        _selectedSeatTypes.value = current
    }

    /** 切换乘客选中状态 */
    fun togglePassenger(code: String) {
        val current = _selectedPassengerCodes.value.toMutableSet()
        if (current.contains(code)) {
            current.remove(code)
        } else {
            current.add(code)
        }
        _selectedPassengerCodes.value = current
    }

    /** 设置是否接受候补 */
    fun setAcceptWaitlist(accept: Boolean) {
        _acceptWaitlist.value = accept
    }

    /**
     * 开始抢票
     * 步骤：
     * 1. 验证配置（座次、乘客）
     * 2. 创建或更新BookingTask
     * 3. 通过StateManager启动任务
     * 4. 更新当前状态
     */
    fun startBooking() {
        val ticket = _ticketInfo.value
        val existingTask = _bookingTask.value

        if (existingTask != null) {
            startExistingTask(existingTask)
            return
        }

        if (ticket == null) {
            showError("车次信息异常")
            return
        }

        if (_selectedSeatTypes.value.isEmpty()) {
            showError("请选择座次偏好")
            return
        }

        if (_selectedPassengerCodes.value.isEmpty()) {
            showError("请选择乘车人")
            return
        }

        viewModelScope.launch(exceptionHandler) {
            showLoading()

            val seatTypes = _selectedSeatTypes.value.toList()
            val primarySeatType = seatTypes.first()
            val primarySeatInfo = _seatList.value.find { it.seatType == primarySeatType }

            val selectedPassengers = _passengers.value.filter {
                it.code in _selectedPassengerCodes.value
            }

            val task = BookingTask(
                trainNumber = ticket.trainCode,
                trainNo = ticket.trainNo,
                departureStation = ticket.fromStation,
                departureStationName = ticket.fromStation,
                arrivalStation = ticket.toStation,
                arrivalStationName = ticket.toStation,
                departureDate = "",
                departureTime = ticket.startTime,
                arrivalTime = ticket.arriveTime,
                seatType = primarySeatType,
                seatTypeName = primarySeatInfo?.seatTypeName ?: "",
                passengerIds = selectedPassengers.map { it.code },
                passengerNames = selectedPassengers.map { it.passenger_name },
                strategy = _selectedStrategy.value.name,
                refreshInterval = _refreshInterval.value,
                maxRetryCount = _maxRetryCount.value,
                seatPreferences = seatTypes,
                acceptWaitlist = _acceptWaitlist.value
            )

            val result = ticketRepository.createBookingTask(task)

            hideLoading()

            result.fold(
                onSuccess = { taskId ->
                    val createdTask = task.copy(id = taskId)
                    _bookingTask.value = createdTask
                    bookingStateManager.startTask(createdTask)
                    _currentStatus.value = BookingStatus.MONITORING
                    showSuccess("抢票任务已启动")
                },
                onFailure = { error ->
                    showError(error.message ?: "创建任务失败")
                }
            )
        }
    }

    /**
     * 启动已有任务
     * 步骤：
     * 1. 更新任务配置
     * 2. 通过StateManager启动
     * 3. 更新当前状态
     */
    private fun startExistingTask(task: BookingTask) {
        viewModelScope.launch(exceptionHandler) {
            showLoading()

            val updatedTask = task.copy(
                strategy = _selectedStrategy.value.name,
                refreshInterval = _refreshInterval.value,
                maxRetryCount = _maxRetryCount.value,
                seatPreferences = _selectedSeatTypes.value.toList(),
                acceptWaitlist = _acceptWaitlist.value,
                isActive = true,
                status = BookingStatus.MONITORING.name
            )

            ticketRepository.updateBookingTask(updatedTask)

            bookingStateManager.startTask(updatedTask)
            _bookingTask.value = updatedTask
            _currentStatus.value = BookingStatus.MONITORING

            hideLoading()
            showSuccess("抢票任务已重新启动")
        }
    }

    /**
     * 停止抢票
     * 步骤：
     * 1. 通过StateManager停止任务
     * 2. 更新当前状态
     */
    fun stopBooking() {
        val taskId = _bookingTask.value?.id ?: return

        viewModelScope.launch(exceptionHandler) {
            bookingStateManager.stopTask(taskId)
            _currentStatus.value = BookingStatus.CANCELLED
            showSuccess("抢票已停止")
        }
    }

    /** 获取策略描述文本 */
    fun getStrategyDescription(strategy: BookingStrategyType): String {
        return when (strategy) {
            BookingStrategyType.NORMAL -> "固定间隔查询，适合日常抢票"
            BookingStrategyType.HIGH_SPEED -> "短间隔查询+快速下单，适合热门车次"
            BookingStrategyType.EXTREME -> "多线程并发查询+下单，极热门车次冲刺"
            BookingStrategyType.SMART -> "根据余票情况动态调整，综合最优策略"
        }
    }

    /** 获取状态显示文本 */
    fun getStatusText(status: BookingStatus): String {
        return when (status) {
            BookingStatus.PENDING -> "等待中"
            BookingStatus.MONITORING -> "监控中"
            BookingStatus.BOOKING -> "抢票中"
            BookingStatus.SUCCESS -> "抢票成功"
            BookingStatus.FAILED -> "抢票失败"
            BookingStatus.CANCELLED -> "已取消"
        }
    }
}
