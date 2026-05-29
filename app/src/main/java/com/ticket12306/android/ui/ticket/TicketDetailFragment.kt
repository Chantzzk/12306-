package com.ticket12306.android.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.databinding.FragmentTicketDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TicketDetailFragment : Fragment() {

    private var _binding: FragmentTicketDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
    }

    /** 初始化视图交互 */
    private fun setupViews() {
        binding.btnBook.setOnClickListener {
            showBookingDialog()
        }
    }

    /** 观察选中车次状态，更新详情页信息 */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedTicket.collect { ticket ->
                    ticket?.let {
                        displayTicketInfo(it)
                    }
                }
            }
        }
    }

    /**
     * 展示车次详细信息
     * 步骤：
     * 1. 显示车次号和车次类型标签
     * 2. 显示出发/到达站和时间
     * 3. 显示历时和隔日标识
     * 4. 显示座次余票列表
     */
    private fun displayTicketInfo(ticket: TicketInfo) {
        binding.apply {
            tvTrainNumber.text = ticket.trainCode
            tvDepartureTime.text = ticket.startTime
            tvArrivalTime.text = ticket.arriveTime
            tvDepartureStation.text = ticket.fromStation
            tvArrivalStation.text = ticket.toStation
            tvDuration.text = ticket.duration
            tvTrainClass.text = ticket.trainClassName

            setupTrainTypeTag(ticket.trainCode)
            setupDayDifference(ticket.dayDifference)
            setupSeatDetailList(ticket)
        }
    }

    /** 设置车次类型标签的颜色和文字 */
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

    /** 设置隔日到达标识 */
    private fun setupDayDifference(dayDifference: String) {
        if (dayDifference != "0" && dayDifference.isNotBlank()) {
            binding.tvDayDifference.apply {
                text = "+${dayDifference}天到达"
                visibility = View.VISIBLE
            }
        } else {
            binding.tvDayDifference.visibility = View.GONE
        }
    }

    /**
     * 设置座次详情列表
     * 步骤：使用LinearLayoutManager展示每个座次的名称、价格和余票
     */
    private fun setupSeatDetailList(ticket: TicketInfo) {
        val seatList = ticket.seatTypes.values.toList()

        binding.recyclerViewSeats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SeatDetailAdapter(seatList)
            isNestedScrollingEnabled = false
        }
    }

    /**
     * 跳转到座次选择页面
     * 步骤：
     * 1. 获取当前选中的车次信息
     * 2. 使用Safe Args传递TicketInfo到座次选择页面
     * 3. 执行导航跳转
     */
    private fun showBookingDialog() {
        val ticket = viewModel.selectedTicket.value ?: return
        val direction = TicketDetailFragmentDirections
            .actionTicketDetailFragmentToSeatSelectFragment(ticket)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * 座次详情适配器（用于详情页，每行一个座次）
 * 展示座次名称、价格和余票状态
 */
class SeatDetailAdapter(
    private val seats: List<SeatInfo>
) : RecyclerView.Adapter<SeatDetailAdapter.SeatDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seat_detail, parent, false)
        return SeatDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatDetailViewHolder, position: Int) {
        holder.bind(seats[position])
    }

    override fun getItemCount(): Int = seats.size

    class SeatDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /** 绑定座次详情信息 */
        fun bind(seat: SeatInfo) {
            val tvName: TextView = itemView.findViewById(R.id.tv_seat_name)
            val tvPrice: TextView = itemView.findViewById(R.id.tv_seat_price)
            val tvRemain: TextView = itemView.findViewById(R.id.tv_seat_remain)

            tvName.text = seat.seatTypeName
            tvPrice.text = "¥${String.format("%.0f", seat.price)}"

            val (remainText, remainColor) = getSeatRemainDisplay(seat)
            tvRemain.text = remainText
            tvRemain.setTextColor(remainColor)
        }

        /**
         * 获取座次余票的展示文本和颜色
         * 规则：
         * - 可购买且余票>10：绿色"有"
         * - 可购买且0<余票<=10：橙色显示数量
         * - 可购买但无票：灰色"无"
         * - 不可购买：红色"候补"
         */
        private fun getSeatRemainDisplay(seat: SeatInfo): Pair<String, Int> {
            val context = itemView.context
            return if (seat.canBuy) {
                when {
                    seat.remainTicket > 10 -> {
                        "有" to context.resources.getColor(R.color.ticket_enough, null)
                    }
                    seat.remainTicket > 0 -> {
                        "${seat.remainTicket}张" to context.resources.getColor(R.color.ticket_few, null)
                    }
                    else -> {
                        "无" to context.resources.getColor(R.color.ticket_none, null)
                    }
                }
            } else {
                "候补" to context.resources.getColor(R.color.ticket_sold_out, null)
            }
        }
    }
}
