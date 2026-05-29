package com.ticket12306.android.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.OrderInfo
import com.ticket12306.android.databinding.ItemOrderBinding

class OrderAdapter(
    private val onItemClick: (OrderInfo) -> Unit
) : ListAdapter<OrderInfo, OrderAdapter.OrderViewHolder>(OrderDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(
        private val binding: ItemOrderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * 绑定订单数据到视图
         * 步骤：
         * 1. 显示车次号、出发/到达站、时间
         * 2. 显示乘客信息、座位信息、票价
         * 3. 根据订单状态设置状态标签颜色
         * 4. 设置点击事件跳转到订单详情
         */
        fun bind(order: OrderInfo) {
            binding.tvTrainCode.text = order.train_code
            binding.tvFromStation.text = order.from_station
            binding.tvToStation.text = order.to_station
            binding.tvStartTime.text = order.start_time
            binding.tvArriveTime.text = order.arrive_time
            binding.tvTrainDate.text = order.train_date
            binding.tvTicketPrice.text = "¥${order.ticket_price}"

            val passengerInfo = buildString {
                append(order.passenger_name)
                append(" | ")
                append(order.seat_name)
                append(" | ")
                append(order.coach_name)
            }
            binding.tvPassengerInfo.text = passengerInfo

            bindOrderStatus(order.order_status, order.pay_status)

            binding.root.setOnClickListener {
                onItemClick(order)
            }
        }

        /**
         * 根据订单状态设置状态标签文本和背景色
         * 步骤：判断支付状态和订单状态，分别设置对应颜色
         */
        private fun bindOrderStatus(orderStatus: String, payStatus: String) {
            val context = binding.root.context
            val (text, bgColor) = when {
                payStatus == "0" || orderStatus == "待支付" ->
                    context.getString(R.string.order_status_unpaid) to context.getColor(R.color.warning)
                payStatus == "1" || orderStatus == "已支付" ->
                    context.getString(R.string.order_status_paid) to context.getColor(R.color.success)
                orderStatus == "已取消" ->
                    context.getString(R.string.order_status_cancelled) to context.getColor(R.color.secondary_text)
                else -> orderStatus to context.getColor(R.color.info)
            }

            binding.tvOrderStatus.text = text
            binding.tvOrderStatus.setBackgroundColor(bgColor)
        }
    }

    companion object OrderDiffCallback : DiffUtil.ItemCallback<OrderInfo>() {
        override fun areItemsTheSame(oldItem: OrderInfo, newItem: OrderInfo): Boolean {
            return oldItem.sequence_no == newItem.sequence_no
        }

        override fun areContentsTheSame(oldItem: OrderInfo, newItem: OrderInfo): Boolean {
            return oldItem == newItem
        }
    }
}
