package com.ticket12306.android.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage.asSharedFlow()

    private val _successMessage = MutableSharedFlow<String>()
    val successMessage: SharedFlow<String> = _successMessage.asSharedFlow()

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            _isLoading.value = false
            _errorMessage.emit(throwable.message ?: "未知错误")
        }
    }

    protected fun showLoading() {
        _isLoading.value = true
    }

    protected fun hideLoading() {
        _isLoading.value = false
    }

    protected fun showError(message: String) {
        viewModelScope.launch {
            _errorMessage.emit(message)
        }
    }

    protected fun showSuccess(message: String) {
        viewModelScope.launch {
            _successMessage.emit(message)
        }
    }

    protected inline fun <T> launchWithLoading(crossinline block: suspend () -> Result<T>) {
        viewModelScope.launch(exceptionHandler) {
            showLoading()
            val result = block()
            hideLoading()

            result.fold(
                onSuccess = {},
                onFailure = { showError(it.message ?: "操作失败") }
            )
        }
    }
}
