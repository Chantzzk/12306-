package com.ticket12306.android.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ticket12306.android.R
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.databinding.FragmentSeatSelectBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SeatSelectFragment : Fragment() {

    private var _binding: FragmentSeatSelectBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SeatSelectViewModel by viewModels()

    private lateinit var seatSelectAdapter: SeatSelectAdapter
    private lateinit var passengerSelectAdapter: PassengerSelectAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeatSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapters()
        setupRecyclerViews()
        setupClickListeners()
        observeState()
    }

    /** 初始化适配器，设置座次点击和乘客选择回调 */
    private fun setupAdapters() {
        seatSelectAdapter = SeatSelectAdapter { item, _ ->
            viewModel.selectSeat(item.seatInfo.seatType)
        }

        passengerSelectAdapter = PassengerSelectAdapter { passenger, isChecked ->
            viewModel.togglePassenger(passenger, isChecked)
        }
    }

    /** 初始化RecyclerView */
    private fun setupRecyclerViews() {
        binding.recyclerViewSeatSelect.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = seatSelectAdapter
            isNestedScrollingEnabled = false
        }

        binding.recyclerViewPassengerSelect.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = passengerSelectAdapter
            isNestedScrollingEnabled = false
        }
    }

    /** 设置按钮点击事件 */
    private fun setupClickListeners() {
        binding.btnConfirmSelect.setOnClickListener {
            viewModel.confirmSelect()
        }
    }

    /** 观察ViewModel状态变化，更新UI */
    private fun observeState() {
        observeTicketInfo()
        observeSeatItems()
        observePassengers()
        observeSelectedSeat()
        observeConfirmState()
        observeMessages()
    }

    /**
     * 观察车次信息，更新车次基本信息展示
     * 步骤：
     * 1. 显示车次号
     * 2. 设置车次类型标签
     * 3. 显示出发/到达站和时间
     * 4. 显示历时
     */
    private fun observeTicketInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ticketInfo.collect { ticket ->
                    ticket?.let { displayTicketInfo(it) }
                }
            }
        }
    }

    /**
     * 展示车次基本信息
     * 步骤：
     * 1. 设置车次号
     * 2. 设置车次类型标签颜色和文字
     * 3. 设置出发/到达时间和站名
     * 4. 设置历时
     */
    private fun displayTicketInfo(ticket: TicketInfo) {
        binding.apply {
            tvTrainNumber.text = ticket.trainCode
            tvDepartureTime.text = ticket.startTime
            tvArrivalTime.text = ticket.arriveTime
            tvDepartureStation.text = ticket.fromStation
            tvArrivalStation.text = ticket.toStation
            tvDuration.text = ticket.duration
        }
        setupTrainTypeTag(ticket.trainCode)
    }

    /**
     * 设置车次类型标签的颜色和文字
     * 步骤：根据车次号首字母判断类型，设置对应颜色和显示名
     */
    private fun setupTrainTypeTag(trainCode: String) {
        val firstChar = trainCode.firstOrNull()?.uppercaseChar()
        val (text, bgColor) = when (firstChar) {
            'G' -> "高铁" to R.color.train_type_high_speed
            'D' -> "动车" to R.color.train_type_electric
            'Z' -> "直达" to R.color.train_type_direct
            'T' -> "特快" to R.color.train_type_express
            'K' -> "快速" to R.color.train_type_fast
            else -> "其他" to R.color.train_type_other
        }

        binding.tvTrainTypeTag.apply {
            this.text = text
            val ctx = this.context
            this.setBackgroundColor(ctx.resources.getColor(bgColor, null))
        }
    }

    /** 观察座次列表，更新适配器 */
    private fun observeSeatItems() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.seatItems.collect { items ->
                    seatSelectAdapter.submitList(items)
                }
            }
        }
    }

    /**
     * 观察乘客列表，更新适配器和空状态
     * 步骤：
     * 1. 更新乘客列表数据
     * 2. 控制空状态提示显示
     */
    private fun observePassengers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.passengers.collect { passengers ->
                    passengerSelectAdapter.submitList(passengers)
                    binding.tvNoPassenger.visibility =
                        if (passengers.isEmpty()) View.VISIBLE else View.GONE
                    binding.recyclerViewPassengerSelect.visibility =
                        if (passengers.isEmpty()) View.GONE else View.VISIBLE
                }
            }
        }
    }

    /**
     * 观察已选座次信息，更新提示文本、按钮文本和乘客计数
     * 步骤：
     * 1. 更新座次选择提示文本
     * 2. 根据候补状态更新确认按钮文本
     * 3. 更新乘客选中计数
     */
    private fun observeSelectedSeat() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedSeatTypeName.collect { seatName ->
                    if (seatName.isNotBlank()) {
                        val waitlistTag = if (viewModel.isWaitlist.value) "（候补）" else ""
                        binding.tvSeatSelectedInfo.text =
                            getString(R.string.seat_selected, seatName + waitlistTag)
                        binding.tvSeatSelectedInfo.setTextColor(
                            resources.getColor(R.color.primary, null)
                        )
                    } else {
                        binding.tvSeatSelectedInfo.text = getString(R.string.seat_select_hint)
                        binding.tvSeatSelectedInfo.setTextColor(
                            resources.getColor(R.color.secondary_text, null)
                        )
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isWaitlist.collect { isWaitlist ->
                    binding.btnConfirmSelect.text =
                        if (isWaitlist) getString(R.string.seat_waitlist_tag) + getString(R.string.confirm_select)
                        else getString(R.string.confirm_select)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedPassengerNames.collect { names ->
                    if (names.isNotEmpty()) {
                        binding.tvPassengerCount.text =
                            getString(R.string.passenger_count, names.size)
                        binding.tvPassengerCount.visibility = View.VISIBLE
                    } else {
                        binding.tvPassengerCount.visibility = View.GONE
                    }
                }
            }
        }
    }

    /**
     * 观察确认状态，处理确认结果
     * 步骤：
     * 1. Success：弹出提示，返回上一页
     * 2. Error：弹出错误提示
     */
    private fun observeConfirmState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.confirmResult.collect { state ->
                    when (state) {
                        is ConfirmState.Success -> {
                            val msg = if (state.isWaitlist) {
                                "候补任务创建成功：${state.seatTypeName}"
                            } else {
                                "抢票任务创建成功：${state.seatTypeName}"
                            }
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                            viewModel.resetConfirmState()
                            findNavController().popBackStack()
                        }
                        is ConfirmState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                            viewModel.resetConfirmState()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    /** 观察错误和成功消息 */
    private fun observeMessages() {
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
