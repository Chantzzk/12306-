package com.ticket12306.android.ui.booking

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
import com.google.android.material.chip.Chip
import com.ticket12306.android.R
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.BookingStrategyType
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.databinding.FragmentBookingConfigBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookingConfigFragment : Fragment() {

    private var _binding: FragmentBookingConfigBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingConfigViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingConfigBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupSliders()
        observeState()
    }

    /** 设置按钮点击事件 */
    private fun setupClickListeners() {
        binding.btnStartBooking.setOnClickListener { viewModel.startBooking() }
        binding.btnStopBooking.setOnClickListener { viewModel.stopBooking() }

        binding.chipGroupStrategy.setOnCheckedStateChangeListener { _, checkedIds ->
            val strategy = when (checkedIds.firstOrNull()) {
                R.id.chip_normal -> BookingStrategyType.NORMAL
                R.id.chip_high_speed -> BookingStrategyType.HIGH_SPEED
                R.id.chip_extreme -> BookingStrategyType.EXTREME
                R.id.chip_smart -> BookingStrategyType.SMART
                else -> BookingStrategyType.NORMAL
            }
            viewModel.setStrategy(strategy)
        }

        binding.chipGroupInterval.setOnCheckedStateChangeListener { _, checkedIds ->
            val interval = when (checkedIds.firstOrNull()) {
                R.id.chip_interval_3 -> 3
                R.id.chip_interval_5 -> 5
                R.id.chip_interval_10 -> 10
                R.id.chip_interval_30 -> 30
                else -> 5
            }
            viewModel.setRefreshInterval(interval)
        }

        binding.switchWaitlist.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAcceptWaitlist(isChecked)
        }
    }

    /** 设置滑块监听 */
    private fun setupSliders() {
        binding.sliderRetryCount.addOnChangeListener { _, value, _ ->
            val count = value.toInt()
            viewModel.setMaxRetryCount(count)
            binding.tvRetryCountValue.text = "${count}次"
        }
    }

    /** 观察ViewModel状态变化，更新UI */
    private fun observeState() {
        observeTicketInfo()
        observeBookingTask()
        observeStrategy()
        observeInterval()
        observeRetryCount()
        observeSeatList()
        observeSelectedSeats()
        observePassengers()
        observeSelectedPassengers()
        observeWaitlist()
        observeStatus()
        observeMessages()
    }

    /** 观察车次信息，更新车次卡片 */
    private fun observeTicketInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ticketInfo.collect { ticket ->
                    ticket?.let { displayTicketInfo(it) }
                }
            }
        }
    }

    /** 展示车次基本信息 */
    private fun displayTicketInfo(ticket: com.ticket12306.android.data.model.TicketInfo) {
        binding.apply {
            tvTrainNumber.text = ticket.trainCode
            tvRoute.text = "${ticket.fromStation} → ${ticket.toStation}"
            tvTime.text = "${ticket.startTime} - ${ticket.arriveTime}"
            tvDuration.text = ticket.duration
        }
        setupTrainTypeTag(ticket.trainCode)
    }

    /** 设置车次类型标签 */
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
            setBackgroundColor(resources.getColor(bgColor, null))
        }
    }

    /** 观察已有任务配置，恢复UI状态 */
    private fun observeBookingTask() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookingTask.collect { task ->
                    task?.let {
                        binding.tvTrainNumber.text = it.trainNumber
                        binding.tvRoute.text = "${it.departureStationName} → ${it.arrivalStationName}"
                        binding.tvTime.text = "${it.departureTime} - ${it.arrivalTime}"
                        setupTrainTypeTag(it.trainNumber)
                    }
                }
            }
        }
    }

    /** 观察策略选择，更新描述 */
    private fun observeStrategy() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedStrategy.collect { strategy ->
                    binding.tvStrategyDesc.text = viewModel.getStrategyDescription(strategy)
                }
            }
        }
    }

    /** 观察刷新间隔 */
    private fun observeInterval() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.refreshInterval.collect { interval ->
                    val chipId = when (interval) {
                        3 -> R.id.chip_interval_3
                        10 -> R.id.chip_interval_10
                        30 -> R.id.chip_interval_30
                        else -> R.id.chip_interval_5
                    }
                    if (binding.chipGroupInterval.checkedChipId != chipId) {
                        binding.chipGroupInterval.check(chipId)
                    }
                }
            }
        }
    }

    /** 观察重试次数 */
    private fun observeRetryCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.maxRetryCount.collect { count ->
                    if (binding.sliderRetryCount.value != count.toFloat()) {
                        binding.sliderRetryCount.value = count.toFloat()
                    }
                    binding.tvRetryCountValue.text = "${count}次"
                }
            }
        }
    }

    /** 观察座次列表，动态生成座次Chips */
    private fun observeSeatList() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.seatList.collect { seats ->
                    updateSeatChips(seats)
                }
            }
        }
    }

    /** 动态生成座次选择Chips */
    private fun updateSeatChips(seats: List<SeatInfo>) {
        binding.chipGroupSeat.removeAllViews()
        seats.forEach { seat ->
            val chip = Chip(requireContext()).apply {
                text = "${seat.seatTypeName}(余${seat.remainTicket})"
                isCheckable = true
                isChecked = seat.seatType in viewModel.selectedSeatTypes.value
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.toggleSeatType(seat.seatType)
                }
            }
            binding.chipGroupSeat.addView(chip)
        }
    }

    /** 观察已选座次 */
    private fun observeSelectedSeats() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedSeatTypes.collect { selectedTypes ->
                    updateSeatChipStates(selectedTypes)
                }
            }
        }
    }

    /** 更新座次Chip的选中状态 */
    private fun updateSeatChipStates(selectedTypes: Set<String>) {
        val chipGroup = binding.chipGroupSeat
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip ?: continue
            val seatInfo = viewModel.seatList.value.getOrNull(i) ?: continue
            chip.isChecked = seatInfo.seatType in selectedTypes
        }
    }

    /** 观察乘客列表，动态生成乘客Chips */
    private fun observePassengers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.passengers.collect { passengers ->
                    updatePassengerChips(passengers)
                }
            }
        }
    }

    /** 动态生成乘客选择Chips */
    private fun updatePassengerChips(passengers: List<Passenger>) {
        binding.chipGroupPassenger.removeAllViews()
        passengers.forEach { passenger ->
            val chip = Chip(requireContext()).apply {
                text = passenger.passenger_name
                isCheckable = true
                isChecked = passenger.code in viewModel.selectedPassengerCodes.value
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.togglePassenger(passenger.code)
                }
            }
            binding.chipGroupPassenger.addView(chip)
        }
    }

    /** 观察已选乘客 */
    private fun observeSelectedPassengers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedPassengerCodes.collect { selectedCodes ->
                    updatePassengerChipStates(selectedCodes)
                }
            }
        }
    }

    /** 更新乘客Chip的选中状态 */
    private fun updatePassengerChipStates(selectedCodes: Set<String>) {
        val chipGroup = binding.chipGroupPassenger
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as? Chip ?: continue
            val passenger = viewModel.passengers.value.getOrNull(i) ?: continue
            chip.isChecked = passenger.code in selectedCodes
        }
    }

    /** 观察候补开关 */
    private fun observeWaitlist() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.acceptWaitlist.collect { accept ->
                    if (binding.switchWaitlist.isChecked != accept) {
                        binding.switchWaitlist.isChecked = accept
                    }
                }
            }
        }
    }

    /**
     * 观察抢票状态，更新按钮显示
     * 步骤：
     * 1. 根据状态切换开始/停止按钮
     * 2. 禁用/启用配置项
     */
    private fun observeStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentStatus.collect { status ->
                    val isRunning = status == BookingStatus.MONITORING || status == BookingStatus.BOOKING
                    binding.btnStartBooking.visibility = if (isRunning) View.GONE else View.VISIBLE
                    binding.btnStopBooking.visibility = if (isRunning) View.VISIBLE else View.GONE

                    val isEnabled = !isRunning
                    binding.cardStrategy.isEnabled = isEnabled
                    binding.cardSettings.isEnabled = isEnabled
                    binding.cardSeatPassenger.isEnabled = isEnabled
                }
            }
        }
    }

    /** 观察消息通知 */
    private fun observeMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collect { message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.successMessage.collect { message ->
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
