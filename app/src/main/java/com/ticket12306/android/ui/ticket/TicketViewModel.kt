package com.ticket12306.android.ui.ticket

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.ui.base.BaseViewModel
import com.ticket12306.android.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : BaseViewModel() {

    // ==================== 查询参数状态 ====================

    private val _fromStationCode = MutableStateFlow("")
    val fromStationCode: StateFlow<String> = _fromStationCode.asStateFlow()

    private val _fromStationName = MutableStateFlow("")
    val fromStationName: StateFlow<String> = _fromStationName.asStateFlow()

    private val _toStationCode = MutableStateFlow("")
    val toStationCode: StateFlow<String> = _toStationCode.asStateFlow()

    private val _toStationName = MutableStateFlow("")
    val toStationName: StateFlow<String> = _toStationName.asStateFlow()

    private val _selectedDate = MutableStateFlow(DateUtils.getToday())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // ==================== 查询结果状态 ====================

    private val _allTickets = MutableStateFlow<List<TicketInfo>>(emptyList())
    private val _displayTickets = MutableStateFlow<List<TicketInfo>>(emptyList())
    val displayTickets: StateFlow<List<TicketInfo>> = _displayTickets.asStateFlow()

    private val _queryState = MutableStateFlow<QueryState>(QueryState.Idle)
    val queryState: StateFlow<QueryState> = _queryState.asStateFlow()

    private val _selectedTicket = MutableStateFlow<TicketInfo?>(null)
    val selectedTicket: StateFlow<TicketInfo?> = _selectedTicket.asStateFlow()

    // ==================== 筛选状态 ====================

    private val _selectedTrainType = MutableStateFlow(TrainType.ALL)
    val selectedTrainType: StateFlow<TrainType> = _selectedTrainType.asStateFlow()

    private val _selectedTimePeriod = MutableStateFlow<TimePeriod?>(null)
    val selectedTimePeriod: StateFlow<TimePeriod?> = _selectedTimePeriod.asStateFlow()

    private val _onlyHasTicket = MutableStateFlow(false)
    val onlyHasTicket: StateFlow<Boolean> = _onlyHasTicket.asStateFlow()

    // ==================== 排序状态 ====================

    private val _sortField = MutableStateFlow(SortField.DEPARTURE_TIME)
    val sortField: StateFlow<SortField> = _sortField.asStateFlow()

    private val _sortOrder = MutableStateFlow(SortOrder.ASCENDING)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    // ==================== 抢票任务状态 ====================

    private val _bookingTasks = MutableStateFlow<List<BookingTask>>(emptyList())
    val bookingTasks: StateFlow<List<BookingTask>> = _bookingTasks.asStateFlow()

    private val _activeTaskCount = MutableStateFlow(0)
    val activeTaskCount: StateFlow<Int> = _activeTaskCount.asStateFlow()

    // ==================== 结果数量 ====================

    private val _totalResultCount = MutableStateFlow(0)
    val totalResultCount: StateFlow<Int> = _totalResultCount.asStateFlow()

    init {
        loadBookingTasks()
        observeFilterAndSort()
    }

    // ==================== 查询参数管理 ====================

    /** 设置出发站信息 */
    fun setFromStation(code: String, name: String) {
        _fromStationCode.value = code
        _fromStationName.value = name
    }

    /** 设置到达站信息 */
    fun setToStation(code: String, name: String) {
        _toStationCode.value = code
        _toStationName.value = name
    }

    /** 交换出发站和到达站 */
    fun swapStations() {
        val tempCode = _fromStationCode.value
        val tempName = _fromStationName.value
        _fromStationCode.value = _toStationCode.value
        _fromStationName.value = _toStationName.value
        _toStationCode.value = tempCode
        _toStationName.value = tempName
    }

    /** 设置出发日期 */
    fun setSelectedDate(date: String) {
        if (DateUtils.isValidBookingDate(date) || date == DateUtils.getToday()) {
            _selectedDate.value = date
        }
    }

    /** 切换到前一天 */
    fun navigateToPreviousDay() {
        val currentDate = _selectedDate.value
        val today = DateUtils.getToday()
        if (currentDate > today) {
            _selectedDate.value = DateUtils.getOffsetDate(currentDate, -1)
        }
    }

    /** 切换到后一天 */
    fun navigateToNextDay() {
        val currentDate = _selectedDate.value
        val maxDate = DateUtils.getMaxBookingDate()
        if (currentDate < maxDate) {
            _selectedDate.value = DateUtils.getOffsetDate(currentDate, 1)
        }
    }

    /** 判断是否可以前往前一天 */
    fun canNavigateToPreviousDay(): Boolean {
        return _selectedDate.value > DateUtils.getToday()
    }

    /** 判断是否可以前往后一天 */
    fun canNavigateToNextDay(): Boolean {
        return _selectedDate.value < DateUtils.getMaxBookingDate()
    }

    // ==================== 车票查询 ====================

    /** 执行车票查询，步骤：1.验证参数 2.发起网络请求 3.处理结果 4.应用筛选和排序 */
    fun queryTickets() {
        val fromCode = _fromStationCode.value
        val toCode = _toStationCode.value
        val date = _selectedDate.value

        if (fromCode.isBlank()) {
            showError("请选择出发站")
            return
        }
        if (toCode.isBlank()) {
            showError("请选择到达站")
            return
        }
        if (fromCode == toCode) {
            showError("出发站和到达站不能相同")
            return
        }
        if (!DateUtils.isValidBookingDate(date) && date != DateUtils.getToday()) {
            showError("出发日期不在预售期内")
            return
        }

        viewModelScope.launch(exceptionHandler) {
            _queryState.value = QueryState.Loading
            showLoading()

            val result = ticketRepository.queryTickets(fromCode, toCode, date)

            hideLoading()

            result.fold(
                onSuccess = { ticketList ->
                    _allTickets.value = ticketList
                    _totalResultCount.value = ticketList.size
                    _queryState.value = QueryState.Success(ticketList)
                    applyFilterAndSort()
                },
                onFailure = { error ->
                    _allTickets.value = emptyList()
                    _displayTickets.value = emptyList()
                    _totalResultCount.value = 0
                    _queryState.value = QueryState.Error(error.message ?: "查询失败")
                    showError(error.message ?: "查询失败")
                }
            )
        }
    }

    // ==================== 筛选逻辑 ====================

    /** 设置车次类型筛选 */
    fun setTrainTypeFilter(trainType: TrainType) {
        _selectedTrainType.value = trainType
    }

    /** 设置出发时间段筛选 */
    fun setTimePeriodFilter(timePeriod: TimePeriod?) {
        _selectedTimePeriod.value = timePeriod
    }

    /** 设置仅看有票筛选 */
    fun setOnlyHasTicket(onlyHasTicket: Boolean) {
        _onlyHasTicket.value = onlyHasTicket
    }

    /** 重置所有筛选条件 */
    fun resetFilters() {
        _selectedTrainType.value = TrainType.ALL
        _selectedTimePeriod.value = null
        _onlyHasTicket.value = false
    }

    // ==================== 排序逻辑 ====================

    /** 设置排序字段，如果与当前字段相同则切换升降序，否则默认升序 */
    fun setSortField(field: SortField) {
        if (_sortField.value == field) {
            _sortOrder.value = if (_sortOrder.value == SortOrder.ASCENDING) {
                SortOrder.DESCENDING
            } else {
                SortOrder.ASCENDING
            }
        } else {
            _sortField.value = field
            _sortOrder.value = SortOrder.ASCENDING
        }
    }

    // ==================== 筛选与排序组合应用 ====================

    /** 监听筛选和排序条件变化，自动重新计算展示列表 */
    private fun observeFilterAndSort() {
        viewModelScope.launch {
            combine(
                _allTickets,
                _selectedTrainType,
                _selectedTimePeriod,
                _onlyHasTicket,
                _sortField,
                _sortOrder
            ) { _ -> applyFilterAndSort() }
                .collect {}
        }
    }

    /**
     * 应用筛选和排序逻辑
     * 步骤：
     * 1. 根据车次类型筛选
     * 2. 根据出发时间段筛选
     * 3. 根据是否有票筛选
     * 4. 根据排序字段和排序方向排序
     * 5. 更新展示列表
     */
    private fun applyFilterAndSort() {
        var filtered = _allTickets.value.toList()

        // 1. 按车次类型筛选
        filtered = filterByTrainType(filtered, _selectedTrainType.value)

        // 2. 按出发时间段筛选
        _selectedTimePeriod.value?.let { period ->
            filtered = filterByTimePeriod(filtered, period)
        }

        // 3. 按是否有票筛选
        if (_onlyHasTicket.value) {
            filtered = filtered.filter { ticket ->
                ticket.seatTypes.values.any { it.remainTicket > 0 && it.canBuy }
            }
        }

        // 4. 排序
        filtered = sortTickets(filtered, _sortField.value, _sortOrder.value)

        _displayTickets.value = filtered
    }

    /**
     * 根据车次类型筛选车次
     * 步骤：根据车次号首字母判断类型，G-高铁、D-动车、Z-直达、T-特快、K-快速
     */
    private fun filterByTrainType(tickets: List<TicketInfo>, trainType: TrainType): List<TicketInfo> {
        if (trainType == TrainType.ALL) return tickets

        return tickets.filter { ticket ->
            val code = ticket.trainCode
            val firstChar = code.firstOrNull()?.uppercaseChar()
            when (trainType) {
                TrainType.HIGH_SPEED -> firstChar == 'G'
                TrainType.ELECTRIC -> firstChar == 'D'
                TrainType.DIRECT -> firstChar == 'Z'
                TrainType.EXPRESS -> firstChar == 'T'
                TrainType.FAST -> firstChar == 'K'
                TrainType.OTHER -> firstChar !in setOf('G', 'D', 'Z', 'T', 'K')
                TrainType.ALL -> true
            }
        }
    }

    /**
     * 根据出发时间段筛选车次
     * 步骤：将出发时间解析为分钟数，判断是否落在指定时间段内
     */
    private fun filterByTimePeriod(tickets: List<TicketInfo>, period: TimePeriod): List<TicketInfo> {
        return tickets.filter { ticket ->
            val minutes = DateUtils.parseTimeToMinutes(ticket.startTime)
            minutes in period.startMinute..period.endMinute
        }
    }

    /**
     * 对车次列表进行排序
     * 步骤：根据排序字段提取比较值，按排序方向排列
     */
    private fun sortTickets(tickets: List<TicketInfo>, field: SortField, order: SortOrder): List<TicketInfo> {
        val sorted = when (field) {
            SortField.DEPARTURE_TIME -> tickets.sortedBy { DateUtils.parseTimeToMinutes(it.startTime) }
            SortField.ARRIVAL_TIME -> tickets.sortedBy { DateUtils.parseTimeToMinutes(it.arriveTime) }
            SortField.DURATION -> tickets.sortedBy { DateUtils.parseDurationToMinutes(it.duration) }
        }
        return if (order == SortOrder.DESCENDING) sorted.reversed() else sorted
    }

    // ==================== 车次选择与抢票 ====================

    /** 选中某个车次 */
    fun selectTicket(ticket: TicketInfo) {
        _selectedTicket.value = ticket
    }

    /** 创建抢票任务 */
    fun createBookingTask(
        ticket: TicketInfo,
        seatType: String,
        seatTypeName: String,
        passengerIds: List<String>,
        passengerNames: List<String>
    ) {
        viewModelScope.launch(exceptionHandler) {
            showLoading()

            val task = BookingTask(
                trainNumber = ticket.trainCode,
                trainNo = ticket.trainNo,
                departureStation = ticket.fromStation,
                departureStationName = ticket.fromStation,
                arrivalStation = ticket.toStation,
                arrivalStationName = ticket.toStation,
                departureDate = _selectedDate.value,
                departureTime = ticket.startTime,
                arrivalTime = ticket.arriveTime,
                seatType = seatType,
                seatTypeName = seatTypeName,
                passengerIds = passengerIds,
                passengerNames = passengerNames
            )

            val result = ticketRepository.createBookingTask(task)

            hideLoading()

            result.fold(
                onSuccess = {
                    showSuccess("抢票任务创建成功")
                    loadBookingTasks()
                },
                onFailure = { error ->
                    showError(error.message ?: "创建任务失败")
                }
            )
        }
    }

    fun activateTask(taskId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val result = ticketRepository.activateBookingTask(taskId)
            result.fold(
                onSuccess = {
                    showSuccess("任务已激活")
                    loadBookingTasks()
                },
                onFailure = { error ->
                    showError(error.message ?: "激活失败")
                }
            )
        }
    }

    fun deactivateTask(taskId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val result = ticketRepository.deactivateBookingTask(taskId)
            result.fold(
                onSuccess = {
                    showSuccess("任务已暂停")
                    loadBookingTasks()
                },
                onFailure = { error ->
                    showError(error.message ?: "暂停失败")
                }
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(exceptionHandler) {
            val result = ticketRepository.deleteBookingTask(taskId)
            result.fold(
                onSuccess = {
                    showSuccess("任务已删除")
                    loadBookingTasks()
                },
                onFailure = { error ->
                    showError(error.message ?: "删除失败")
                }
            )
        }
    }

    private fun loadBookingTasks() {
        viewModelScope.launch {
            ticketRepository.allBookingTasks.collect { tasks ->
                _bookingTasks.value = tasks
                _activeTaskCount.value = tasks.count { it.isActive }
            }
        }
    }

    /** 清空查询结果 */
    fun clearTickets() {
        _allTickets.value = emptyList()
        _displayTickets.value = emptyList()
        _totalResultCount.value = 0
        _queryState.value = QueryState.Idle
    }
}

// ==================== 查询状态密封类 ====================

sealed class QueryState {
    object Idle : QueryState()
    object Loading : QueryState()
    data class Success(val tickets: List<TicketInfo>) : QueryState()
    data class Error(val message: String) : QueryState()
}

// ==================== 车次类型枚举 ====================

enum class TrainType(val displayName: String) {
    ALL("全部"),
    HIGH_SPEED("高铁"),
    ELECTRIC("动车"),
    DIRECT("直达"),
    EXPRESS("特快"),
    FAST("快速"),
    OTHER("其他")
}

// ==================== 时间段枚举 ====================

enum class TimePeriod(val displayName: String, val startMinute: Int, val endMinute: Int) {
    MORNING("00:00-06:00", 0, 359),
    FORENOON("06:00-12:00", 360, 719),
    AFTERNOON("12:00-18:00", 720, 1079),
    EVENING("18:00-24:00", 1080, 1439)
}

// ==================== 排序字段枚举 ====================

enum class SortField(val displayName: String) {
    DEPARTURE_TIME("出发时间"),
    ARRIVAL_TIME("到达时间"),
    DURATION("历时")
}

// ==================== 排序方向枚举 ====================

enum class SortOrder {
    ASCENDING,
    DESCENDING
}
