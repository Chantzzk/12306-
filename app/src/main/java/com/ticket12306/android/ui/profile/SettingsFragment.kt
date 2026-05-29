package com.ticket12306.android.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ticket12306.android.R
import com.ticket12306.android.data.model.BookingStrategyType
import com.ticket12306.android.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupControls()
        observeState()
    }

    /**
     * 初始化设置控件
     * 步骤：
     * 1. 设置通知开关监听
     * 2. 设置刷新间隔滑块监听
     * 3. 设置最大重试次数滑块监听
     * 4. 设置抢票策略下拉框
     * 5. 设置接受候补开关监听
     */
    private fun setupControls() {
        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotificationEnabled(isChecked)
        }

        binding.sliderRefreshInterval.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setDefaultRefreshInterval(value.toInt())
            }
        }

        binding.sliderMaxRetry.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                viewModel.setDefaultMaxRetry(value.toInt())
            }
        }

        val strategyNames = BookingStrategyType.values().map { type ->
            when (type) {
                BookingStrategyType.NORMAL -> getString(R.string.strategy_normal)
                BookingStrategyType.HIGH_SPEED -> getString(R.string.strategy_high_speed)
                BookingStrategyType.EXTREME -> getString(R.string.strategy_extreme)
                BookingStrategyType.SMART -> getString(R.string.strategy_smart)
            }
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, strategyNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStrategy.adapter = adapter

        binding.spinnerStrategy.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = BookingStrategyType.values()[position].name
                viewModel.setDefaultStrategy(selected)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        binding.switchAcceptWaitlist.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDefaultAcceptWaitlist(isChecked)
        }
    }

    /**
     * 观察ViewModel状态变化，同步UI
     * 步骤：根据设置状态更新控件值，避免循环触发
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (binding.switchNotification.isChecked != state.notificationEnabled) {
                        binding.switchNotification.isChecked = state.notificationEnabled
                    }

                    binding.tvRefreshInterval.text = "${state.defaultRefreshInterval}秒"
                    if (!binding.sliderRefreshInterval.isPressed) {
                        binding.sliderRefreshInterval.value = state.defaultRefreshInterval.toFloat()
                    }

                    binding.tvMaxRetry.text = "${state.defaultMaxRetry}次"
                    if (!binding.sliderMaxRetry.isPressed) {
                        binding.sliderMaxRetry.value = state.defaultMaxRetry.toFloat()
                    }

                    val strategyIndex = BookingStrategyType.values().indexOfFirst {
                        it.name == state.defaultStrategy
                    }.coerceAtLeast(0)
                    if (binding.spinnerStrategy.selectedItemPosition != strategyIndex) {
                        binding.spinnerStrategy.setSelection(strategyIndex)
                    }

                    if (binding.switchAcceptWaitlist.isChecked != state.defaultAcceptWaitlist) {
                        binding.switchAcceptWaitlist.isChecked = state.defaultAcceptWaitlist
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
