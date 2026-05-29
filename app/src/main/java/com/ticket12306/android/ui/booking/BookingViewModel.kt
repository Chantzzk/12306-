package com.ticket12306.android.ui.booking

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.booking.BookingStateManager
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.OrderInfo
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val bookingStateManager: BookingStateManager
) : BaseViewModel() {

    private val _bookingTasks = MutableStateFlow<List<BookingTask>>(emptyList())
    val bookingTasks: StateFlow<List<BookingTask>> = _bookingTasks.asStateFlow()

    private val _orders = MutableStateFlow<List<OrderInfo>>(emptyList())
    val orders: StateFlow<List<OrderInfo>> = _orders.asStateFlow()

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private val _taskStatuses = MutableStateFlow<Map<Long, BookingStatus>>(emptyMap())
    val taskStatuses: StateFlow<Map<Long, BookingStatus>> = _taskStatuses.asStateFlow()

    init {
        loadBookingTasks()
        observeTaskStatuses()
    }

    private fun loadBookingTasks() {
        viewModelScope.launch {
            ticketRepository.activeBookingTasks.collect { tasks ->
                _bookingTasks.value = tasks
            }
        }
    }

    /** 观察BookingStateManager中的状态变化 */
    private fun observeTaskStatuses() {
        viewModelScope.launch {
            bookingStateManager.taskStates.collect { states ->
                _taskStatuses.value = states
            }
        }
    }

    /**
     * 启动抢票任务
     * 步骤：
     * 1. 通过StateManager启动
     * 2. 更新UI状态
     */
    fun startBooking(task: BookingTask) {
        viewModelScope.launch(exceptionHandler) {
            _bookingState.value = BookingState.Booking(task.id)
            showLoading()

            bookingStateManager.startTask(task)

            hideLoading()
            _bookingState.value = BookingState.Idle
            showSuccess("抢票任务已启动: ${task.trainNumber}")
        }
    }

    /**
     * 停止抢票任务
     * 步骤：
     * 1. 通过StateManager停止
     * 2. 更新UI状态
     */
    fun stopBooking(taskId: Long) {
        viewModelScope.launch(exceptionHandler) {
            bookingStateManager.stopTask(taskId)
            _bookingState.value = BookingState.Idle
            showSuccess("已停止抢票")
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch(exceptionHandler) {
            bookingStateManager.deleteTask(taskId)
            showSuccess("任务已删除")
        }
    }

    fun queryOrders(startDate: String, endDate: String) {
        viewModelScope.launch(exceptionHandler) {
            showLoading()

            val result = ticketRepository.queryMyOrders(startDate, endDate)

            hideLoading()

            result.fold(
                onSuccess = { orderList ->
                    _orders.value = orderList
                },
                onFailure = { error ->
                    showError(error.message ?: "查询订单失败")
                }
            )
        }
    }

    /** 刷新任务列表，重新加载抢票任务数据 */
    fun refreshTasks() {
        viewModelScope.launch {
            ticketRepository.activeBookingTasks.collect { tasks ->
                _bookingTasks.value = tasks
            }
        }
    }

    fun resetState() {
        _bookingState.value = BookingState.Idle
    }

    /** 获取任务状态文本 */
    fun getStatusText(taskId: Long): String {
        val status = _taskStatuses.value[taskId] ?: BookingStatus.PENDING
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

sealed class BookingState {
    object Idle : BookingState()
    data class Booking(val taskId: Long) : BookingState()
    data class Success(val trainNumber: String, val orderSequence: String) : BookingState()
    data class Error(val message: String) : BookingState()
}
