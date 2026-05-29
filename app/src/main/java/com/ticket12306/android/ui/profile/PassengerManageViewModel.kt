package com.ticket12306.android.ui.profile

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.data.repository.UserRepository
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PassengerManageUiState(
    val passengers: List<Passenger> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class PassengerManageViewModel @Inject constructor(
    private val database: AppDatabase,
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(PassengerManageUiState())
    val uiState: StateFlow<PassengerManageUiState> = _uiState.asStateFlow()

    init {
        loadLocalPassengers()
    }

    /**
     * 加载本地缓存的乘客列表
     * 步骤：从数据库读取乘客列表并更新UI状态
     */
    private fun loadLocalPassengers() {
        viewModelScope.launch {
            database.passengerDao().getAllPassengers().collect { passengers ->
                _uiState.update { it.copy(passengers = passengers) }
            }
        }
    }

    /**
     * 从服务器刷新乘客列表
     * 步骤：
     * 1. 设置刷新状态
     * 2. 调用Repository获取乘客数据
     * 3. 成功则自动通过Flow更新（Room自动通知）
     * 4. 失败则显示错误信息
     */
    fun refreshPassengers() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isRefreshing = true) }

            val result = userRepository.fetchPassengers()

            _uiState.update { it.copy(isRefreshing = false) }

            result.fold(
                onSuccess = {
                    showSuccess("乘客列表已更新")
                },
                onFailure = { error ->
                    showError(error.message ?: "获取乘客信息失败")
                }
            )
        }
    }

    /**
     * 删除乘客
     * 步骤：
     * 1. 从数据库删除乘客记录
     * 2. 成功则显示提示
     */
    fun deletePassenger(passenger: Passenger) {
        viewModelScope.launch(exceptionHandler) {
            database.passengerDao().deletePassenger(passenger)
            showSuccess("已删除乘客: ${passenger.passenger_name}")
        }
    }
}
