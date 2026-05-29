package com.ticket12306.android.ui.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.ticket12306.android.R
import com.ticket12306.android.databinding.ActivityLoginBinding
import com.ticket12306.android.ui.main.MainActivity
import com.ticket12306.android.util.NetworkUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    /**
     * Android 13+ 通知权限请求 launcher
     * 步骤：用户授权或拒绝后处理结果
     */
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // 无论授权与否都不影响登录流程
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermission()
        setupViews()
        observeState()
        viewModel.loadCaptcha()
    }

    /**
     * 请求通知权限（Android 13+必需）
     * 步骤：
     * 1. 检查当前权限状态
     * 2. 未授权则发起权限请求
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }

    /**
     * 初始化界面控件和事件监听
     * 步骤：
     * 1. 绑定各输入框的文本变化监听，实时触发表单验证
     * 2. 设置验证码图片点击刷新
     * 3. 设置登录按钮点击事件
     * 4. 设置键盘回车键动作
     */
    private fun setupViews() {
        binding.etUsername.addTextChangedListener { text ->
            viewModel.onUsernameChanged(text?.toString() ?: "")
            updateLoginButtonState()
        }

        binding.etPassword.addTextChangedListener { text ->
            viewModel.onPasswordChanged(text?.toString() ?: "")
            updateLoginButtonState()
        }

        binding.etCaptcha.addTextChangedListener { text ->
            viewModel.onCaptchaChanged(text?.toString() ?: "")
            updateLoginButtonState()
        }

        // 验证码图片区域点击刷新
        binding.flCaptcha.setOnClickListener {
            viewModel.loadCaptcha()
        }

        // 登录按钮点击
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        // 验证码输入框回车键触发登录
        binding.etCaptcha.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin()
                true
            } else {
                false
            }
        }
    }

    /**
     * 观察ViewModel中的状态变化
     * 包括：登录状态、验证码图片、表单验证、验证码加载、全局加载、错误/成功消息
     */
    private fun observeState() {
        // 观察登录状态
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginState.collect { state ->
                    when (state) {
                        is LoginState.Idle -> {
                            hideLoading()
                            updateLoginButtonState()
                        }
                        is LoginState.Loading -> {
                            showLoading()
                        }
                        is LoginState.Success -> {
                            hideLoading()
                            navigateToMain()
                        }
                        is LoginState.Error -> {
                            hideLoading()
                            showErrorSnackbar(state.message)
                            binding.etCaptcha.text?.clear()
                        }
                    }
                }
            }
        }

        // 观察验证码图片（Base64字符串 → Bitmap）
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.captchaImage.collect { base64Image ->
                    base64Image?.let {
                        decodeAndShowCaptcha(it)
                    } ?: run {
                        binding.ivCaptcha.setImageBitmap(null)
                        binding.tvCaptchaRefresh.visibility = View.VISIBLE
                    }
                }
            }
        }

        // 观察表单验证状态，更新输入框错误提示
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginFormState.collect { formState ->
                    binding.tilUsername.error = formState.usernameError
                    binding.tilPassword.error = formState.passwordError
                    binding.tilCaptcha.error = formState.captchaError
                }
            }
        }

        // 观察验证码加载状态
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.captchaLoading.collect { isLoading ->
                    binding.pbCaptcha.visibility = if (isLoading) View.VISIBLE else View.GONE
                    binding.tvCaptchaRefresh.visibility =
                        if (isLoading) View.GONE else if (viewModel.captchaImage.value == null) View.VISIBLE else View.GONE
                }
            }
        }

        // 观察全局加载状态
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    if (isLoading) {
                        showLoading()
                    } else {
                        hideLoading()
                    }
                }
            }
        }

        // 观察错误消息
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    showErrorSnackbar(message)
                }
            }
        }

        // 观察成功消息
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.successMessage.collect { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // 观察已登录状态，如果已登录则直接跳转
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoggedIn.collect { isLoggedIn ->
                    if (isLoggedIn && viewModel.loginState.value !is LoginState.Loading) {
                        navigateToMain()
                    }
                }
            }
        }
    }

    /**
     * 尝试登录
     * 步骤：
     * 1. 先隐藏软键盘
     * 2. 检查网络连接
     * 3. 收集输入框数据
     * 4. 调用ViewModel登录方法
     */
    private fun attemptLogin() {
        hideKeyboard()

        if (!NetworkUtils.isNetworkAvailable(this)) {
            showErrorSnackbar(getString(R.string.error_network))
            return
        }

        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val captcha = binding.etCaptcha.text.toString().trim()

        viewModel.login(username, password, captcha)
    }

    /**
     * 将Base64编码的验证码图片解码并显示到ImageView
     * 步骤：
     * 1. 去除Base64前缀（如有data:image/...格式）
     * 2. Base64解码为字节数组
     * 3. 解码为Bitmap并设置到ImageView
     * 4. 隐藏刷新提示文字
     * 5. 如果解码失败则显示刷新提示
     */
    private fun decodeAndShowCaptcha(base64Str: String) {
        try {
            val pureBase64 = if (base64Str.contains(",")) {
                base64Str.substring(base64Str.indexOf(",") + 1)
            } else {
                base64Str
            }

            val decodedBytes = Base64.decode(pureBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            if (bitmap != null) {
                binding.ivCaptcha.setImageBitmap(bitmap)
                binding.tvCaptchaRefresh.visibility = View.GONE
            } else {
                binding.ivCaptcha.setImageBitmap(null)
                binding.tvCaptchaRefresh.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            binding.ivCaptcha.setImageBitmap(null)
            binding.tvCaptchaRefresh.visibility = View.VISIBLE
        }
    }

    /**
     * 显示加载状态
     * 显示进度条和遮罩层，禁用登录按钮
     */
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.overlay.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    /**
     * 隐藏加载状态
     * 隐藏进度条和遮罩层，恢复登录按钮状态
     */
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.overlay.visibility = View.GONE
        updateLoginButtonState()
    }

    /**
     * 根据表单输入状态更新登录按钮是否可用
     * 所有字段非空时才启用
     */
    private fun updateLoginButtonState() {
        val username = binding.etUsername.text?.toString()?.trim() ?: ""
        val password = binding.etPassword.text?.toString()?.trim() ?: ""
        val captcha = binding.etCaptcha.text?.toString()?.trim() ?: ""

        binding.btnLogin.isEnabled =
            username.isNotBlank() && password.isNotBlank() && captcha.isNotBlank() &&
                    viewModel.loginState.value !is LoginState.Loading
    }

    /**
     * 显示错误Snackbar
     */
    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.error))
            .setTextColor(getColor(R.color.white))
            .show()
    }

    /**
     * 隐藏软键盘
     */
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 跳转到主界面并结束当前Activity
     */
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        /**
         * 创建LoginActivity的Intent
         */
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }
}
