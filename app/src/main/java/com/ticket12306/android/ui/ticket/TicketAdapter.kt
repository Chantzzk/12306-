package com.ticket12306.android.ui.ticket

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.databinding.ItemTicketBinding

class TicketAdapter(
    private val onItemClick: (TicketInfo) -> Unit
) : ListAdapter<TicketInfo, TicketAdapter.TicketViewHolder>(TicketDiffCallback()) {

    /** 是否启用列表项入场动画 */
    private var shouldAnimateItems = true

    /** 设置是否启用入场动画，首次加载时启用，后续更新禁用 */
    fun setAnimateItems(animate: Boolean) {
        shouldAnimateItems = animate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemTicketBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TicketViewHolder(binding, onItemClick)
    }

    /**
     * 绑定数据到ViewHolder
     * 步骤：
     * 1. 绑定车次数据
     * 2. 首次加载时播放入场动画
     */
    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (shouldAnimateItems) {
            applyItemAnimation(holder, position)
        }
    }

    /**
     * 为列表项应用入场动画
     * 步骤：从底部滑入+淡入，根据位置添加递增延迟
     */
    private fun applyItemAnimation(holder: TicketViewHolder, position: Int) {
        val animation = AnimationUtils.loadAnimation(
            holder.itemView.context,
            R.anim.item_slide_in_bottom
        )
        animation.startOffset = (position * 50L).coerceAtMost(300)
        holder.itemView.startAnimation(animation)
    }

    class TicketViewHolder(
        private val binding: ItemTicketBinding,
        private val onItemClick: (TicketInfo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /** 绑定车次数据到视图，步骤：1.基本信息 2.车次类型标签 3.隔日标识 4.座次余票列表 */
        fun bind(ticket: TicketInfo) {
            binding.apply {
                tvTrainNumber.text = ticket.trainCode
                tvDepartureTime.text = ticket.startTime
                tvArrivalTime.text = ticket.arriveTime
                tvDepartureStation.text = ticket.fromStation
                tvArrivalStation.text = ticket.toStation
                tvDuration.text = ticket.duration

                setupTrainTypeTag(ticket.trainCode)
                setupDayDifference(ticket.dayDifference)
                setupSeatList(ticket)

                root.setOnClickListener {
                    onItemClick(ticket)
                }
            }
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
                val context = this.context
                this.setBackgroundColor(
                    context.resources.getColor(bgColor, null)
                )
            }
        }

        /**
         * 设置隔日到达标识
         * 当dayDifference不为"0"时显示"+N天"标识
         */
        private fun setupDayDifference(dayDifference: String) {
            if (dayDifference != "0" && dayDifference.isNotBlank()) {
                binding.tvDayDifference.apply {
                    text = "+${dayDifference}天"
                    visibility = View.VISIBLE
                }
            } else {
                binding.tvDayDifference.visibility = View.GONE
            }
        }

        /**
         * 设置座次余票列表
         * 步骤：
         * 1. 将seatTypes转为有序列表（按常见座次优先级排列）
         * 2. 使用GridLayoutManager展示座次信息
         * 3. 每个座次显示名称、价格、余票状态
         */
        private fun setupSeatList(ticket: TicketInfo) {
            val seatOrder = listOf(
                "A9", "P", "M", "O",
                "A3", "A4", "A1", "A2",
                "W", "1", "2", "3", "4"
            )

            val seatList = ticket.seatTypes.entries
                .sortedBy { entry ->
                    val index = seatOrder.indexOf(entry.key)
                    if (index >= 0) index else Int.MAX_VALUE
                }
                .map { it.value }

            val spanCount = calculateSpanCount(seatList.size)

            binding.recyclerViewSeats.apply {
                layoutManager = GridLayoutManager(context, spanCount)
                adapter = SeatInfoAdapter(seatList)
                isNestedScrollingEnabled = false
            }
        }

        /** 根据座次数量计算网格列数 */
        private fun calculateSpanCount(seatCount: Int): Int {
            return when {
                seatCount <= 3 -> seatCount
                seatCount <= 6 -> 3
                else -> 4
            }
        }
    }
}

/**
 * 座次余票信息适配器
 * 展示座次名称、价格和余票状态
 */
class SeatInfoAdapter(
    private val seats: List<SeatInfo>
) : RecyclerView.Adapter<SeatInfoAdapter.SeatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seat_info, parent, false)
        return SeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(seats[position])
    }

    override fun getItemCount(): Int = seats.size

    class SeatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /** 绑定座次信息，步骤：1.座次名 2.价格 3.余票状态及颜色 */
        fun bind(seat: SeatInfo) {
            val tvName: TextView = itemView.findViewById(R.id.tv_seat_name)
            val tvPrice: TextView = itemView.findViewById(R.id.tv_seat_price)
            val tvRemain: TextView = itemView.findViewById(R.id.tv_seat_remain)

            tvName.text = seat.seatTypeName
            tvPrice.text = "¥${seat.price.toInt()}"

            val (remainText, remainColor) = getSeatRemainDisplay(seat)
            tvRemain.text = remainText
            tvRemain.setTextColor(remainColor)
        }

        /**
         * 获取座次余票的展示文本和颜色
         * 步骤：根据余票数量和可购买状态判断展示方式
         * - canBuy且余票充足：绿色显示"有"
         * - canBuy且余票较少：橙色显示具体数量
         * - canBuy但无票：灰色显示"无"
         * - 不可购买：红色显示"候补"
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

class TicketDiffCallback : DiffUtil.ItemCallback<TicketInfo>() {
    override fun areItemsTheSame(oldItem: TicketInfo, newItem: TicketInfo): Boolean {
        return oldItem.trainNo == newItem.trainNo
    }

    /**
     * 比较车次内容是否相同
     * 步骤：仅比较影响UI展示的关键字段，避免比较seatTypes Map提高性能
     */
    override fun areContentsTheSame(oldItem: TicketInfo, newItem: TicketInfo): Boolean {
        return oldItem.trainCode == newItem.trainCode &&
                oldItem.startTime == newItem.startTime &&
                oldItem.arriveTime == newItem.arriveTime &&
                oldItem.fromStation == newItem.fromStation &&
                oldItem.toStation == newItem.toStation &&
                oldItem.duration == newItem.duration &&
                oldItem.canWebBuy == newItem.canWebBuy &&
                oldItem.seatTypes.size == newItem.seatTypes.size &&
                oldItem.seatTypes.entries.all { (key, value) ->
                    newItem.seatTypes[key]?.let { newSeat ->
                        value.remainTicket == newSeat.remainTicket &&
                        value.canBuy == newSeat.canBuy
                    } ?: false
                }
    }
}
