package com.ticket12306.android.ui.station

import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.R
import com.ticket12306.android.data.model.Station
import com.ticket12306.android.databinding.ItemStationBinding

class StationAdapter(
    private val onStationClick: (Station) -> Unit
) : ListAdapter<Station, StationAdapter.StationViewHolder>(StationDiffCallback()) {

    /** 当前搜索关键字，用于高亮匹配文本 */
    private var searchQuery: String = ""

    /**
     * 更新搜索关键字
     * 关键字变更后需要刷新所有可见项以更新高亮状态
     */
    fun setSearchQuery(query: String) {
        searchQuery = query
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        val binding = ItemStationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StationViewHolder(binding, onStationClick)
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.bind(getItem(position), searchQuery)
    }

    class StationViewHolder(
        private val binding: ItemStationBinding,
        private val onStationClick: (Station) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * 绑定车站数据到视图
         * 步骤：
         * 1. 设置车站名称（带搜索关键字高亮）
         * 2. 设置车站代码
         * 3. 设置所属城市
         * 4. 设置拼音信息
         * 5. 设置点击事件
         */
        fun bind(station: Station, query: String) {
            binding.apply {
                tvStationName.text = highlightText(station.name, query)
                tvStationCode.text = station.code

                if (!station.city.isNullOrBlank()) {
                    tvCity.text = station.city
                    tvCity.visibility = android.view.View.VISIBLE
                } else {
                    tvCity.visibility = android.view.View.GONE
                }

                tvPinyin.text = station.pinyin

                root.setOnClickListener {
                    onStationClick(station)
                }
            }
        }

        /**
         * 高亮匹配的搜索关键字
         * 在文本中查找关键字出现的位置，使用颜色Span标记
         * 步骤：
         * 1. 关键字为空则原样返回
         * 2. 查找关键字在文本中的所有出现位置
         * 3. 为每个匹配区间设置高亮颜色
         */
        private fun highlightText(text: String, query: String): SpannableString {
            val spannable = SpannableString(text)

            if (query.isBlank()) return spannable

            val lowerText = text.lowercase()
            val lowerQuery = query.lowercase()

            var startIndex = 0
            while (startIndex < lowerText.length) {
                val index = lowerText.indexOf(lowerQuery, startIndex)
                if (index == -1) break

                val highlightColor = itemView.context.getColor(R.color.search_highlight)
                spannable.setSpan(
                    ForegroundColorSpan(highlightColor),
                    index,
                    index + lowerQuery.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                startIndex = index + lowerQuery.length
            }

            return spannable
        }
    }
}

class StationDiffCallback : DiffUtil.ItemCallback<Station>() {
    override fun areItemsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: Station, newItem: Station): Boolean {
        return oldItem == newItem
    }
}
