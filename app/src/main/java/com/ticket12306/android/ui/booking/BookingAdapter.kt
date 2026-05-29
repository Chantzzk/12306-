package com.ticket12306.android.ui.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.BookingStrategyType
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.databinding.ItemBookingTaskBinding

class BookingAdapter(
    private val onStartClick: (BookingTask) -> Unit,
    private val onStopClick: (BookingTask) -> Unit,
    private val onDeleteClick: (BookingTask) -> Unit,
    private val onConfigClick: (BookingTask) -> Unit = {},
    private val getStatusText: (Long) -> String = { "" }
) : ListAdapter<BookingTask, BookingAdapter.BookingViewHolder>(BookingDiffCallback()) {

    /** 是否启用列表项入场动画 */
    private var shouldAnimateItems = true

    /** 设置是否启用入场动画 */
    fun setAnimateItems(animate: Boolean) {
        shouldAnimateItems = animate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookingViewHolder(binding, onStartClick, onStopClick, onDeleteClick, onConfigClick, getStatusText)
    }

    /**
     * 绑定数据到ViewHolder
     * 步骤：
     * 1. 绑定抢票任务数据
     * 2. 首次加载时播放入场动画
     */
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (shouldAnimateItems) {
            val animation = AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.item_slide_in_bottom
            )
            animation.startOffset = (position * 80L).coerceAtMost(400)
            holder.itemView.startAnimation(animation)
        }
    }

    class BookingViewHolder(
        private val binding: ItemBookingTaskBinding,
        private val onStartClick: (BookingTask) -> Unit,
        private val onStopClick: (BookingTask) -> Unit,
        private val onDeleteClick: (BookingTask) -> Unit,
        private val onConfigClick: (BookingTask) -> Unit,
        private val getStatusText: (Long) -> String
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: BookingTask) {
            binding.apply {
                tvTrainNumber.text = task.trainNumber
                tvRoute.text = "${task.departureStationName} → ${task.arrivalStationName}"
                tvDate.text = task.departureDate
                tvTime.text = "${task.departureTime} - ${task.arrivalTime}"
                tvSeatType.text = buildSeatInfoText(task)
                tvPassengers.text = task.passengerNames.joinToString(", ")

                val statusText = getStatusText(task.id)
                if (statusText.isNotBlank()) {
                    tvSeatType.text = "${buildSeatInfoText(task)} · $statusText"
                }

                val isRunning = task.isActive && (
                    task.status == BookingStatus.MONITORING.name ||
                    task.status == BookingStatus.BOOKING.name
                )

                btnStart.visibility = if (isRunning) View.GONE else View.VISIBLE
                btnStop.visibility = if (isRunning) View.VISIBLE else View.GONE

                btnStart.setOnClickListener { onStartClick(task) }
                btnStop.setOnClickListener { onStopClick(task) }
                btnDelete.setOnClickListener { onDeleteClick(task) }
            }
        }

        private fun buildSeatInfoText(task: BookingTask): String {
            val strategyName = try {
                when (BookingStrategyType.valueOf(task.strategy)) {
                    BookingStrategyType.NORMAL -> "普通"
                    BookingStrategyType.HIGH_SPEED -> "高速"
                    BookingStrategyType.EXTREME -> "极限"
                    BookingStrategyType.SMART -> "智能"
                }
            } catch (e: IllegalArgumentException) {
                "普通"
            }

            val seatPrefs = if (task.seatPreferences.size > 1) {
                "${task.seatTypeName}+${task.seatPreferences.size - 1}座次"
            } else {
                task.seatTypeName
            }

            val waitlistTag = if (task.acceptWaitlist) " 候补" else ""

            return "$seatPrefs · $strategyName${waitlistTag} · ${task.refreshInterval}s"
        }
    }
}

class BookingDiffCallback : DiffUtil.ItemCallback<BookingTask>() {
    override fun areItemsTheSame(oldItem: BookingTask, newItem: BookingTask): Boolean {
        return oldItem.id == newItem.id
    }

    /**
     * 比较抢票任务内容是否相同
     * 步骤：仅比较影响UI展示的关键字段，避免比较passengerIds/passengerNames列表
     */
    override fun areContentsTheSame(oldItem: BookingTask, newItem: BookingTask): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.trainNumber == newItem.trainNumber &&
                oldItem.isActive == newItem.isActive &&
                oldItem.status == newItem.status &&
                oldItem.seatTypeName == newItem.seatTypeName &&
                oldItem.strategy == newItem.strategy &&
                oldItem.currentRetryCount == newItem.currentRetryCount &&
                oldItem.refreshInterval == newItem.refreshInterval &&
                oldItem.acceptWaitlist == newItem.acceptWaitlist &&
                oldItem.seatPreferences == newItem.seatPreferences
    }
}
