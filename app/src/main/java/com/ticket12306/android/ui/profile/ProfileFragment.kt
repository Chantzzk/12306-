package com.ticket12306.android.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ticket12306.android.BuildConfig
import com.ticket12306.android.R
import com.ticket12306.android.databinding.FragmentProfileBinding
import com.ticket12306.android.ui.login.LoginActivity
import com.ticket12306.android.ui.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
    }

    /**
     * 初始化视图控件
     * 步骤：
     * 1. 设置版本号
     * 2. 设置乘客管理点击事件（导航到PassengerManageFragment）
     * 3. 设置查询历史点击事件（导航到QueryHistoryFragment）
     * 4. 设置设置点击事件（导航到SettingsFragment）
     * 5. 设置关于点击事件（导航到AboutFragment）
     * 6. 设置退出登录点击事件
     */
    private fun setupViews() {
        binding.tvVersion.text = getString(R.string.version, BuildConfig.VERSION_NAME)

        binding.itemPassengers.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavigationProfileToPassengerManageFragment()
            )
        }

        binding.itemQueryHistory.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavigationProfileToQueryHistoryFragment()
            )
        }

        binding.itemSettings.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavigationProfileToSettingsFragment()
            )
        }

        binding.itemAbout.setOnClickListener {
            findNavController().navigate(
                ProfileFragmentDirections.actionNavigationProfileToAboutFragment()
            )
        }

        binding.itemLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    /**
     * 观察ViewModel状态变化
     * 步骤：
     * 1. 观察登录状态，未登录则跳转到登录页面
     * 2. 观察用户名变化，更新UI
     * 3. 观察手机号变化，更新UI
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoggedIn.collect { isLoggedIn ->
                    updateLoginState(isLoggedIn)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userName.collect { userName ->
                    binding.tvUsername.text = userName ?: "未登录"
                }
            }
        }
    }

    /**
     * 更新登录状态
     * 步骤：未登录时跳转到登录页面并关闭当前页面
     */
    private fun updateLoginState(isLoggedIn: Boolean) {
        if (!isLoggedIn) {
            startActivity(LoginActivity.newIntent(requireContext()))
        }
    }

    /**
     * 显示退出登录确认对话框
     * 步骤：
     * 1. 构建确认对话框
     * 2. 用户确认后调用ViewModel执行退出登录
     */
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setPositiveButton("确定") { _, _ ->
                viewModel.logout()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
