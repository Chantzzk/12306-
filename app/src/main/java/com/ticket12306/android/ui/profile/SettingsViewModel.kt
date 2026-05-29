package com.ticket12306.android.ui.profile

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationEnabled: Boolean = true,
    val defaultRefreshInterval: Int = 5,
    val defaultMaxRetry: Int = 50,
    val defaultStrategy: String = "NORMAL",
    val defaultAcceptWaitlist: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * 加载设置项
     * 步骤：从UserPreferences读取各设置项并更新UI状态
     */
    private fun loadSettings() {
        viewModelScope.launch {
            userPreferences.notificationEnabled.collect { enabled ->
                _uiState.update { it.copy(notificationEnabled = enabled) }
            }
        }

        viewModelScope.launch {
            userPreferences.defaultRefreshInterval.collect { interval ->
                _uiState.update { it.copy(defaultRefreshInterval = interval) }
            }
        }

        viewModelScope.launch {
            userPreferences.defaultMaxRetry.collect { maxRetry ->
                _uiState.update { it.copy(defaultMaxRetry = maxRetry) }
            }
        }

        viewModelScope.launch {
            userPreferences.defaultStrategy.collect { strategy ->
                _uiState.update { it.copy(defaultStrategy = strategy) }
            }
        }

        viewModelScope.launch {
            userPreferences.defaultAcceptWaitlist.collect { accept ->
                _uiState.update { it.copy(defaultAcceptWaitlist = accept) }
            }
        }
    }

    /**
     * 设置通知开关
     */
    fun setNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationEnabled(enabled)
            _uiState.update { it.copy(notificationEnabled = enabled) }
        }
    }

    /**
     * 设置默认刷新间隔
     */
    fun setDefaultRefreshInterval(interval: Int) {
        viewModelScope.launch {
            userPreferences.saveDefaultRefreshInterval(interval)
            _uiState.update { it.copy(defaultRefreshInterval = interval) }
        }
    }

    /**
     * 设置默认最大重试次数
     */
    fun setDefaultMaxRetry(maxRetry: Int) {
        viewModelScope.launch {
            userPreferences.saveDefaultMaxRetry(maxRetry)
            _uiState.update { it.copy(defaultMaxRetry = maxRetry) }
        }
    }

    /**
     * 设置默认抢票策略
     */
    fun setDefaultStrategy(strategy: String) {
        viewModelScope.launch {
            userPreferences.saveDefaultStrategy(strategy)
            _uiState.update { it.copy(defaultStrategy = strategy) }
        }
    }

    /**
     * 设置默认是否接受候补
     */
    fun setDefaultAcceptWaitlist(accept: Boolean) {
        viewModelScope.launch {
            userPreferences.saveDefaultAcceptWaitlist(accept)
            _uiState.update { it.copy(defaultAcceptWaitlist = accept) }
        }
    }
}
