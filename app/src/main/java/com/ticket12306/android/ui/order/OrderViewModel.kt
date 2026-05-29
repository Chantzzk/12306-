package com.ticket12306.android.ui.order

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.model.OrderInfo
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.ui.base.BaseViewModel
import com.ticket12306.android.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OrderStatusFilter(val label: String) {
    ALL("全部"),
    UNPAID("待支付"),
    PAID("已支付"),
    CANCELLED("已取消")
}

data class OrderUiState(
    val allOrders: List<OrderInfo> = emptyList(),
    val currentFilter: OrderStatusFilter = OrderStatusFilter.ALL,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val cancelSuccess: Boolean = false
) {
    val filteredOrders: List<OrderInfo>
        get() = when (currentFilter) {
            OrderStatusFilter.ALL -> allOrders
            OrderStatusFilter.UNPAID -> allOrders.filter { it.pay_status == "0" || it.order_status == "待支付" }
            OrderStatusFilter.PAID -> allOrders.filter { it.pay_status == "1" || it.order_status == "已支付" }
            OrderStatusFilter.CANCELLED -> allOrders.filter { it.order_status == "已取消" }
        }
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val ticketRepository: TicketRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(OrderUiState())
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    /**
     * 加载订单列表
     * 步骤：
     * 1. 设置加载状态
     * 2. 计算查询日期范围（近30天）
     * 3. 查询全部订单和未支付订单并合并
     * 4. 更新UI状态
     */
    fun loadOrders() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true) }

            val startDate = DateUtils.getDateBeforeDays(30)
            val endDate = DateUtils.getToday()

            val result = ticketRepository.queryMyOrders(startDate, endDate)

            _uiState.update { it.copy(isLoading = false) }

            result.fold(
                onSuccess = { orders ->
                    _uiState.update { it.copy(allOrders = orders) }
                },
                onFailure = { error ->
                    showError(error.message ?: "查询订单失败")
                }
            )
        }
    }

    /**
     * 下拉刷新订单列表
     */
    fun refreshOrders() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isRefreshing = true) }

            val startDate = DateUtils.getDateBeforeDays(30)
            val endDate = DateUtils.getToday()

            val result = ticketRepository.queryMyOrders(startDate, endDate)

            _uiState.update { it.copy(isRefreshing = false) }

            result.fold(
                onSuccess = { orders ->
                    _uiState.update { it.copy(allOrders = orders) }
                },
                onFailure = { error ->
                    showError(error.message ?: "刷新订单失败")
                }
            )
        }
    }

    /**
     * 设置订单状态筛选
     * 步骤：更新当前筛选条件，UI会根据filteredOrders自动更新
     */
    fun setFilter(filter: OrderStatusFilter) {
        _uiState.update { it.copy(currentFilter = filter) }
    }

    /**
     * 取消订单
     * 步骤：
     * 1. 设置加载状态
     * 2. 调用Repository取消订单接口
     * 3. 成功则从列表移除该订单并刷新
     * 4. 失败则显示错误信息
     */
    fun cancelOrder(sequenceNo: String) {
        viewModelScope.launch(exceptionHandler) {
            showLoading()

            val result = ticketRepository.cancelOrder(sequenceNo)

            hideLoading()

            result.fold(
                onSuccess = { success ->
                    if (success) {
                        _uiState.update { state ->
                            val updatedOrders = state.allOrders.map { order ->
                                if (order.sequence_no == sequenceNo) {
                                    order.copy(order_status = "已取消")
                                } else {
                                    order
                                }
                            }
                            state.copy(allOrders = updatedOrders, cancelSuccess = true)
                        }
                        showSuccess("订单已取消")
                    } else {
                        showError("取消订单失败")
                    }
                },
                onFailure = { error ->
                    showError(error.message ?: "取消订单失败")
                }
            )
        }
    }

    /**
     * 重置取消成功标记
     */
    fun resetCancelSuccess() {
        _uiState.update { it.copy(cancelSuccess = false) }
    }
}
