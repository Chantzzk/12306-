package com.ticket12306.android.ui.login

import androidx.lifecycle.viewModelScope
import com.ticket12306.android.data.model.LoginResponse
import com.ticket12306.android.data.repository.UserRepository
import com.ticket12306.android.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _captchaImage = MutableStateFlow<String?>(null)
    val captchaImage: StateFlow<String?> = _captchaImage.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginFormState = MutableStateFlow(LoginFormState())
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()

    private val _captchaLoading = MutableStateFlow(false)
    val captchaLoading: StateFlow<Boolean> = _captchaLoading.asStateFlow()

    val userName = userRepository.userName

    init {
        checkLoginStatus()
    }

    /**
     * 检查用户是否已登录
     * 如果已登录则直接更新状态，避免重复登录
     */
    private fun checkLoginStatus() {
        viewModelScope.launch {
            userRepository.isLoggedIn.collect { loggedIn ->
                _isLoggedIn.value = loggedIn
            }
        }
    }

    /**
     * 加载验证码图片
     * 步骤：
     * 1. 设置加载状态
     * 2. 请求验证码接口
     * 3. 成功则更新验证码图片数据（Base64字符串）
     * 4. 失败则显示错误信息
     */
    fun loadCaptcha() {
        viewModelScope.launch(exceptionHandler) {
            _captchaLoading.value = true
            val result = userRepository.getCaptcha()
            _captchaLoading.value = false

            result.fold(
                onSuccess = { response ->
                    _captchaImage.value = response.image
                },
                onFailure = { error ->
                    _captchaImage.value = null
                    showError(error.message ?: "获取验证码失败")
                }
            )
        }
    }

    /**
     * 执行登录操作
     * 步骤：
     * 1. 先验证表单输入
     * 2. 设置加载状态
     * 3. 调用Repository登录接口
     * 4. 成功则更新登录状态为Success
     * 5. 失败则更新为Error并自动刷新验证码
     */
    fun login(username: String, password: String, captchaAnswer: String) {
        if (!validateForm(username, password, captchaAnswer)) {
            return
        }

        viewModelScope.launch(exceptionHandler) {
            _loginState.value = LoginState.Loading
            showLoading()

            val result = userRepository.login(username, password, captchaAnswer)

            hideLoading()

            result.fold(
                onSuccess = { response ->
                    _loginState.value = LoginState.Success(response)
                    showSuccess("登录成功")
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "登录失败")
                    showError(error.message ?: "登录失败")
                    loadCaptcha()
                }
            )
        }
    }

    /**
     * 退出登录
     * 步骤：
     * 1. 调用Repository退出接口
     * 2. 无论成功失败都清除本地登录信息
     * 3. 重置登录状态
     */
    fun logout() {
        viewModelScope.launch(exceptionHandler) {
            showLoading()
            val result = userRepository.logout()
            hideLoading()

            result.fold(
                onSuccess = {
                    _loginState.value = LoginState.Idle
                    _isLoggedIn.value = false
                    showSuccess("已退出登录")
                },
                onFailure = { error ->
                    showError(error.message ?: "退出登录失败")
                }
            )
        }
    }

    /**
     * 重置登录状态为空闲
     * 用于清除错误或成功状态，恢复初始界面
     */
    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    /**
     * 验证表单输入
     * 规则：
     * - 用户名不能为空且长度至少3位
     * - 密码不能为空且长度至少6位
     * - 验证码不能为空且长度为4位
     * 返回是否全部验证通过
     */
    fun validateForm(username: String, password: String, captcha: String): Boolean {
        val usernameError = when {
            username.isBlank() -> "用户名不能为空"
            username.length < 3 -> "用户名长度至少3位"
            else -> null
        }

        val passwordError = when {
            password.isBlank() -> "密码不能为空"
            password.length < 6 -> "密码长度至少6位"
            else -> null
        }

        val captchaError = when {
            captcha.isBlank() -> "验证码不能为空"
            captcha.length != 4 -> "验证码为4位"
            else -> null
        }

        _loginFormState.value = LoginFormState(
            usernameError = usernameError,
            passwordError = passwordError,
            captchaError = captchaError,
            isDataValid = usernameError == null && passwordError == null && captchaError == null
        )

        return _loginFormState.value.isDataValid
    }

    /**
     * 实时验证用户名输入
     */
    fun onUsernameChanged(username: String) {
        val error = when {
            username.isBlank() -> null
            username.length < 3 -> "用户名长度至少3位"
            else -> null
        }
        _loginFormState.value = _loginFormState.value.copy(usernameError = error)
    }

    /**
     * 实时验证密码输入
     */
    fun onPasswordChanged(password: String) {
        val error = when {
            password.isBlank() -> null
            password.length < 6 -> "密码长度至少6位"
            else -> null
        }
        _loginFormState.value = _loginFormState.value.copy(passwordError = error)
    }

    /**
     * 实时验证验证码输入
     */
    fun onCaptchaChanged(captcha: String) {
        val error = when {
            captcha.isBlank() -> null
            captcha.length > 4 -> "验证码为4位"
            else -> null
        }
        _loginFormState.value = _loginFormState.value.copy(captchaError = error)
    }
}

/**
 * 登录状态密封类
 * Idle - 空闲/初始状态
 * Loading - 登录请求中
 * Success - 登录成功
 * Error - 登录失败
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * 登录表单状态数据类
 * 用于管理各字段的验证错误信息和整体表单有效性
 */
data class LoginFormState(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val captchaError: String? = null,
    val isDataValid: Boolean = false
)
