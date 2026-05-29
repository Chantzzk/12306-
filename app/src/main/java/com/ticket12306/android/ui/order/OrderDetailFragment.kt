package com.ticket12306.android.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ticket12306.android.R
import com.ticket12306.android.databinding.FragmentOrderDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by activityViewModels()
    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindOrderInfo(args.orderInfo)
        setupCancelButton()
        observeState()
    }

    /**
     * 绑定订单信息到视图
     * 步骤：
     * 1. 显示车次号、出发/到达站、时间
     * 2. 显示订单号、乘客信息、座位、票价
     * 3. 根据订单状态设置状态标签和取消按钮可见性
     */
    private fun bindOrderInfo(order: com.ticket12306.android.data.model.OrderInfo) {
        binding.tvTrainCode.text = order.train_code
        binding.tvFromStation.text = order.from_station
        binding.tvToStation.text = order.to_station
        binding.tvStartTime.text = order.start_time
        binding.tvArriveTime.text = order.arrive_time
        binding.tvTrainDate.text = order.train_date
        binding.tvSequenceNo.text = order.sequence_no
        binding.tvPassengerName.text = order.passenger_name
        binding.tvSeatInfo.text = "${order.seat_name} ${order.coach_name}"
        binding.tvTicketType.text = order.ticket_type_name
        binding.tvTicketPrice.text = "¥${order.ticket_price}"

        val isUnpaid = order.pay_status == "0" || order.order_status == "待支付"
        binding.btnCancelOrder.visibility = if (isUnpaid) View.VISIBLE else View.GONE

        val (statusText, bgColor) = when {
            order.pay_status == "0" || order.order_status == "待支付" ->
                getString(R.string.order_status_unpaid) to requireContext().getColor(R.color.warning)
            order.pay_status == "1" || order.order_status == "已支付" ->
                getString(R.string.order_status_paid) to requireContext().getColor(R.color.success)
            order.order_status == "已取消" ->
                getString(R.string.order_status_cancelled) to requireContext().getColor(R.color.secondary_text)
            else -> order.order_status to requireContext().getColor(R.color.info)
        }
        binding.tvOrderStatus.text = statusText
        binding.tvOrderStatus.setBackgroundColor(bgColor)
    }

    /**
     * 设置取消订单按钮点击事件
     * 步骤：点击后弹出确认对话框，确认后调用ViewModel取消订单
     */
    private fun setupCancelButton() {
        binding.btnCancelOrder.setOnClickListener {
            showCancelConfirmDialog()
        }
    }

    /**
     * 显示取消订单确认对话框
     * 步骤：
     * 1. 构建确认对话框
     * 2. 用户点击确定后调用ViewModel取消订单
     * 3. 取消成功后返回上一页
     */
    private fun showCancelConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.cancel_order))
            .setMessage(getString(R.string.cancel_order_confirm))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                viewModel.cancelOrder(args.orderInfo.sequence_no)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * 观察ViewModel状态变化
     * 监听取消成功事件，成功后返回订单列表
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.cancelSuccess) {
                        viewModel.resetCancelSuccess()
                        findNavController().navigateUp()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
