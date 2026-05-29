package com.ticket12306.android.ui.ticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.Passenger

/**
 * 乘客选择适配器
 * 功能：展示乘客列表，支持多选交互（CheckBox）
 * 交互逻辑：点击整行或CheckBox切换选中状态
 */
class PassengerSelectAdapter(
    private val onPassengerCheckChanged: (Passenger, Boolean) -> Unit
) : RecyclerView.Adapter<PassengerSelectAdapter.PassengerSelectViewHolder>() {

    private var passengers: List<Passenger> = emptyList()
    private val selectedCodes = mutableSetOf<String>()

    /** 更新乘客列表数据 */
    fun submitList(newPassengers: List<Passenger>) {
        passengers = newPassengers
        notifyDataSetChanged()
    }

    /** 设置已选中的乘客编码集合 */
    fun setSelectedCodes(codes: Set<String>) {
        selectedCodes.clear()
        selectedCodes.addAll(codes)
        notifyDataSetChanged()
    }

    /** 获取已选中的乘客编码集合 */
    fun getSelectedCodes(): Set<String> = selectedCodes.toSet()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerSelectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_passenger_select, parent, false)
        return PassengerSelectViewHolder(view)
    }

    override fun onBindViewHolder(holder: PassengerSelectViewHolder, position: Int) {
        holder.bind(passengers[position])
    }

    override fun getItemCount(): Int = passengers.size

    inner class PassengerSelectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cbPassenger: CheckBox = itemView.findViewById(R.id.cb_passenger)
        private val tvPassengerName: TextView = itemView.findViewById(R.id.tv_passenger_name)
        private val tvPassengerIdType: TextView = itemView.findViewById(R.id.tv_passenger_id_type)
        private val tvPassengerIdNo: TextView = itemView.findViewById(R.id.tv_passenger_id_no)

        /**
         * 绑定乘客数据到视图
         * 步骤：
         * 1. 显示乘客姓名、证件类型和证件号
         * 2. 根据选中状态设置CheckBox
         * 3. 设置点击事件（整行可点击切换选中）
         */
        fun bind(passenger: Passenger) {
            tvPassengerName.text = passenger.passenger_name
            tvPassengerIdType.text = passenger.passenger_id_type_name

            val maskedIdNo = maskIdNumber(passenger.passenger_id_no)
            tvPassengerIdNo.text = maskedIdNo

            cbPassenger.setOnCheckedChangeListener(null)
            cbPassenger.isChecked = passenger.code in selectedCodes

            cbPassenger.setOnCheckedChangeListener { _, isChecked ->
                handleCheckChanged(passenger, isChecked)
            }

            itemView.setOnClickListener {
                cbPassenger.isChecked = !cbPassenger.isChecked
            }
        }

        /**
         * 处理选中状态变化
         * 步骤：更新内部选中集合，通知外部回调
         */
        private fun handleCheckChanged(passenger: Passenger, isChecked: Boolean) {
            if (isChecked) {
                selectedCodes.add(passenger.code)
            } else {
                selectedCodes.remove(passenger.code)
            }
            onPassengerCheckChanged(passenger, isChecked)
        }

        /** 对证件号进行脱敏处理（保留前3位和后4位） */
        private fun maskIdNumber(idNo: String): String {
            if (idNo.length <= 7) return idNo
            val prefix = idNo.substring(0, 3)
            val suffix = idNo.substring(idNo.length - 4)
            val masked = "*".repeat(idNo.length - 7)
            return "$prefix$masked$suffix"
        }
    }
}
