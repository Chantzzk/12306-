package com.ticket12306.android.ui.ticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.SeatSelectItem
import com.ticket12306.android.data.model.TicketStatus

/**
 * 座次选择适配器
 * 功能：展示座次列表，支持单选交互，区分有票/少量/无票/候补四种状态
 * 交互逻辑：
 * - 有票/少量座次：点击选中，高亮显示
 * - 无票座次：置灰显示，点击可加入候补
 * - 候补座次：特殊标识，点击可选中候补
 */
class SeatSelectAdapter(
    private val onSeatClick: (SeatSelectItem, Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<SeatSelectAdapter.SeatSelectViewHolder>() {

    private var items: List<SeatSelectItem> = emptyList()

    /** 更新座次列表数据 */
    fun submitList(newItems: List<SeatSelectItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatSelectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_seat_select, parent, false)
        return SeatSelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeatSelectViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    inner class SeatSelectViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private val layoutItem: LinearLayout = itemView.findViewById(R.id.layout_seat_item)
        private val tvSeatName: TextView = itemView.findViewById(R.id.tv_seat_name)
        private val tvSeatPrice: TextView = itemView.findViewById(R.id.tv_seat_price)
        private val tvSeatRemain: TextView = itemView.findViewById(R.id.tv_seat_remain)
        private val tvSeatTag: TextView = itemView.findViewById(R.id.tv_seat_tag)

        /**
         * 绑定座次数据到视图
         * 步骤：
         * 1. 显示座次名称和价格
         * 2. 根据余票状态设置余票显示文本和颜色
         * 3. 根据选中状态设置背景样式
         * 4. 设置点击事件
         */
        fun bind(item: SeatSelectItem, position: Int) {
            val seat = item.seatInfo
            val context = itemView.context

            tvSeatName.text = seat.seatTypeName
            tvSeatPrice.text = "¥${String.format("%.0f", seat.price)}"

            setupRemainDisplay(item)
            setupItemStyle(item)
            setupTagDisplay(item)

            layoutItem.setOnClickListener {
                onSeatClick(item, position)
            }
        }

        /**
         * 设置余票显示文本和颜色
         * 规则：
         * - ENOUGH(有票)：绿色"有"
         * - FEW(少量)：橙色显示具体数量
         * - NONE(无票)：灰色"无"
         * - WAITLIST(候补)：红色"候补"
         */
        private fun setupRemainDisplay(item: SeatSelectItem) {
            val context = itemView.context
            val (text, color) = when (item.ticketStatus) {
                TicketStatus.ENOUGH -> {
                    "有" to context.resources.getColor(R.color.ticket_enough, null)
                }
                TicketStatus.FEW -> {
                    "${item.seatInfo.remainTicket}张" to context.resources.getColor(R.color.ticket_few, null)
                }
                TicketStatus.NONE -> {
                    "无" to context.resources.getColor(R.color.ticket_none, null)
                }
                TicketStatus.WAITLIST -> {
                    "候补" to context.resources.getColor(R.color.ticket_sold_out, null)
                }
            }
            tvSeatRemain.text = text
            tvSeatRemain.setTextColor(color)
        }

        /**
         * 设置座次项的整体样式
         * 规则：
         * - 选中状态：蓝色边框+浅蓝背景
         * - 有票/少量未选中：白色背景+灰色边框
         * - 无票未选中：灰色背景+灰色边框，文字置灰
         * - 候补未选中：橙色边框+浅橙背景
         */
        private fun setupItemStyle(item: SeatSelectItem) {
            val context = itemView.context

            if (item.isSelected) {
                layoutItem.setBackgroundResource(R.drawable.bg_seat_item_selected)
                tvSeatName.setTextColor(context.resources.getColor(R.color.primary, null))
                tvSeatPrice.setTextColor(context.resources.getColor(R.color.primary_dark, null))
            } else {
                when (item.ticketStatus) {
                    TicketStatus.NONE -> {
                        layoutItem.setBackgroundResource(R.drawable.bg_seat_item_disabled)
                        tvSeatName.setTextColor(context.resources.getColor(R.color.seat_item_disabled_text, null))
                        tvSeatPrice.setTextColor(context.resources.getColor(R.color.seat_item_disabled_text, null))
                    }
                    TicketStatus.WAITLIST -> {
                        layoutItem.setBackgroundResource(R.drawable.bg_seat_item_waitlist)
                        tvSeatName.setTextColor(context.resources.getColor(R.color.seat_waitlist_text, null))
                        tvSeatPrice.setTextColor(context.resources.getColor(R.color.secondary_text, null))
                    }
                    else -> {
                        layoutItem.setBackgroundResource(R.drawable.bg_seat_item_normal)
                        tvSeatName.setTextColor(context.resources.getColor(R.color.primary_text, null))
                        tvSeatPrice.setTextColor(context.resources.getColor(R.color.secondary_text, null))
                    }
                }
            }
        }

        /**
         * 设置座次标签显示
         * 规则：
         * - 选中状态：显示"已选"标签（蓝色）
         * - 候补未选中：显示"候补"标签（橙色）
         * - 无票未选中：显示"无票"标签（灰色）
         * - 其他：隐藏标签
         */
        private fun setupTagDisplay(item: SeatSelectItem) {
            val context = itemView.context

            if (item.isSelected) {
                tvSeatTag.visibility = View.VISIBLE
                tvSeatTag.text = "已选"
                tvSeatTag.setTextColor(context.resources.getColor(R.color.white, null))
                tvSeatTag.setBackgroundResource(R.color.primary)
            } else {
                when (item.ticketStatus) {
                    TicketStatus.WAITLIST -> {
                        tvSeatTag.visibility = View.VISIBLE
                        tvSeatTag.text = "候补"
                        tvSeatTag.setTextColor(context.resources.getColor(R.color.seat_waitlist_text, null))
                        tvSeatTag.setBackgroundResource(R.color.seat_waitlist_bg)
                    }
                    TicketStatus.NONE -> {
                        tvSeatTag.visibility = View.VISIBLE
                        tvSeatTag.text = "无票"
                        tvSeatTag.setTextColor(context.resources.getColor(R.color.ticket_none, null))
                        tvSeatTag.setBackgroundResource(R.color.seat_item_disabled_bg)
                    }
                    else -> {
                        tvSeatTag.visibility = View.GONE
                    }
                }
            }
        }
    }
}
