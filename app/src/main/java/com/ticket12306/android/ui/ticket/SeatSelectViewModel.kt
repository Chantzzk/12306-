package com.ticket12306.android.ui.ticket

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.data.model.SeatSelectItem
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.data.model.TicketStatus
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.data.repository.UserRepository
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeatSelectViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val ticketRepository: TicketRepository,
    private val userRepository: UserRepository,
    private val database: AppDatabase
) : BaseViewModel() {

    // ==================== 座次排序优先级 ====================

    companion object {
        private val SEAT_ORDER = listOf(
            "A9", "P", "M", "O",
            "A3", "A4", "A1", "A2",
            "W", "1", "2", "3", "4"
        )
    }

    // ==================== 车次信息状态 ====================

    private val _ticketInfo = MutableStateFlow<TicketInfo?>(null)
    val ticketInfo: StateFlow<TicketInfo?> = _ticketInfo.asStateFlow()

    // ==================== 座次选择状态 ====================

    private val _seatItems = MutableStateFlow<List<SeatSelectItem>>(emptyList())
    val seatItems: StateFlow<List<SeatSelectItem>> = _seatItems.asStateFlow()

    private val _selectedSeatType = MutableStateFlow<String?>(null)
    val selectedSeatType: StateFlow<String?> = _selectedSeatType.asStateFlow()

    private val _selectedSeatTypeName = MutableStateFlow("")
    val selectedSeatTypeName: StateFlow<String> = _selectedSeatTypeName.asStateFlow()

    private val _isWaitlist = MutableStateFlow(false)
    val isWaitlist: StateFlow<Boolean> = _isWaitlist.asStateFlow()

    // ==================== 乘客选择状态 ====================

    private val _passengers = MutableStateFlow<List<Passenger>>(emptyList())
    val passengers: StateFlow<List<Passenger>> = _passengers.asStateFlow()

    private val _selectedPassengerCodes = MutableStateFlow<Set<String>>(emptySet())
    val selectedPassengerCodes: StateFlow<Set<String>> = _selectedPassengerCodes.asStateFlow()

    private val _selectedPassengerNames = MutableStateFlow<List<String>>(emptyList())
    val selectedPassengerNames: StateFlow<List<String>> = _selectedPassengerNames.asStateFlow()

    // ==================== 确认状态 ====================

    private val _confirmResult = MutableStateFlow<ConfirmState>(ConfirmState.Idle)
    val confirmResult: StateFlow<ConfirmState> = _confirmResult.asStateFlow()

    init {
        val ticket = savedStateHandle.get<TicketInfo>("ticketInfo")
        ticket?.let {
            _ticketInfo.value = it
            initSeatItems(it)
        }
        loadPassengers()
    }

    /**
     * 初始化座次列表
     * 步骤：
     * 1. 将seatTypes按优先级排序
     * 2. 为每个座次创建SeatSelectItem（默认未选中）
     * 3. 更新座次列表状态
     */
    private fun initSeatItems(ticket: TicketInfo) {
        val seatList = ticket.seatTypes.entries
            .sortedBy { entry ->
                val index = SEAT_ORDER.indexOf(entry.key)
                if (index >= 0) index else Int.MAX_VALUE
            }
            .map { entry ->
                SeatSelectItem(seatInfo = entry.value, isSelected = false)
            }
        _seatItems.value = seatList
    }

    /**
     * 加载乘客列表
     * 步骤：
     * 1. 先从本地数据库监听乘客数据变化
     * 2. 尝试从网络刷新乘客数据
     */
    private fun loadPassengers() {
        viewModelScope.launch {
            database.passengerDao().getAllPassengers().collect { passengerList ->
                if (passengerList.isNotEmpty()) {
                    _passengers.value = passengerList
                }
            }
        }

        viewModelScope.launch(exceptionHandler) {
            val result = userRepository.fetchPassengers()
            result.onSuccess { passengerList ->
                _passengers.value = passengerList
            }
        }
    }

    /**
     * 选择座次（单选）
     * 步骤：
     * 1. 取消之前选中的座次
     * 2. 选中新座次
     * 3. 更新选中座次信息
     * 4. 判断是否为候补
     */
    fun selectSeat(seatType: String) {
        val currentItems = _seatItems.value.toMutableList()
        val newItems = currentItems.map { item ->
            val isSelected = item.seatInfo.seatType == seatType
            item.copy(isSelected = isSelected)
        }

        _seatItems.value = newItems

        val selectedItem = newItems.find { it.isSelected }
        if (selectedItem != null) {
            _selectedSeatType.value = selectedItem.seatInfo.seatType
            _selectedSeatTypeName.value = selectedItem.seatInfo.seatTypeName
            _isWaitlist.value = selectedItem.ticketStatus == TicketStatus.NONE ||
                    selectedItem.ticketStatus == TicketStatus.WAITLIST
        } else {
            _selectedSeatType.value = null
            _selectedSeatTypeName.value = ""
            _isWaitlist.value = false
        }
    }

    /**
     * 切换乘客选中状态
     * 步骤：
     * 1. 添加或移除乘客编码
     * 2. 更新选中乘客名称列表
     */
    fun togglePassenger(passenger: Passenger, isChecked: Boolean) {
        val currentCodes = _selectedPassengerCodes.value.toMutableSet()
        if (isChecked) {
            currentCodes.add(passenger.code)
        } else {
            currentCodes.remove(passenger.code)
        }
        _selectedPassengerCodes.value = currentCodes

        updateSelectedPassengerNames()
    }

    /** 更新选中乘客名称列表 */
    private fun updateSelectedPassengerNames() {
        val codes = _selectedPassengerCodes.value
        val names = _passengers.value
            .filter { it.code in codes }
            .map { it.passenger_name }
        _selectedPassengerNames.value = names
    }

    /**
     * 确认选择，创建抢票任务
     * 步骤：
     * 1. 验证座次是否已选
     * 2. 验证乘客是否已选
     * 3. 构建BookingTask
     * 4. 调用Repository创建抢票任务
     * 5. 更新确认状态
     */
    fun confirmSelect() {
        val ticket = _ticketInfo.value
        val seatType = _selectedSeatType.value
        val seatTypeName = _selectedSeatTypeName.value
        val passengerCodes = _selectedPassengerCodes.value.toList()
        val passengerNames = _selectedPassengerNames.value

        if (ticket == null) {
            showError("车次信息异常")
            return
        }
        if (seatType == null) {
            showError("请选择座次类型")
            return
        }
        if (passengerCodes.isEmpty()) {
            showError("请选择乘车人")
            return
        }

        viewModelScope.launch(exceptionHandler) {
            _confirmResult.value = ConfirmState.Loading
            showLoading()

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
                seatType = seatType,
                seatTypeName = seatTypeName,
                passengerIds = passengerCodes,
                passengerNames = passengerNames
            )

            val result = ticketRepository.createBookingTask(task)

            hideLoading()

            result.fold(
                onSuccess = { taskId ->
                    _confirmResult.value = ConfirmState.Success(
                        seatTypeName = seatTypeName,
                        passengerNames = passengerNames,
                        isWaitlist = _isWaitlist.value
                    )
                    showSuccess(if (_isWaitlist.value) "候补任务创建成功" else "抢票任务创建成功")
                },
                onFailure = { error ->
                    _confirmResult.value = ConfirmState.Error(error.message ?: "创建任务失败")
                    showError(error.message ?: "创建任务失败")
                }
            )
        }
    }

    /** 重置确认状态 */
    fun resetConfirmState() {
        _confirmResult.value = ConfirmState.Idle
    }
}

/** 确认状态密封类 */
sealed class ConfirmState {
    object Idle : ConfirmState()
    object Loading : ConfirmState()
    data class Success(
        val seatTypeName: String,
        val passengerNames: List<String>,
        val isWaitlist: Boolean
    ) : ConfirmState()
    data class Error(val message: String) : ConfirmState()
}
