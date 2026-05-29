package com.ticket12306.android.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.entity.QueryHistoryEntity
import com.ticket12306.android.databinding.FragmentQueryHistoryBinding
import com.ticket12306.android.databinding.ItemQueryHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class QueryHistoryFragment : Fragment() {

    private var _binding: FragmentQueryHistoryBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var database: AppDatabase

    private lateinit var historyAdapter: QueryHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQueryHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeHistory()
    }

    /**
     * 初始化查询历史列表RecyclerView
     */
    private fun setupRecyclerView() {
        historyAdapter = QueryHistoryAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    /**
     * 观察查询历史数据变化
     * 步骤：从数据库读取当前用户的查询历史（userId=0为默认），更新列表和空状态
     */
    private fun observeHistory() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                database.queryHistoryDao().getRecentQueryHistory(0, 50).collect { histories ->
                    historyAdapter.submitList(histories)
                    binding.emptyView.visibility =
                        if (histories.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class QueryHistoryAdapter : ListAdapter<QueryHistoryEntity, QueryHistoryAdapter.ViewHolder>(
    QueryHistoryDiffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQueryHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemQueryHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * 绑定查询历史数据到视图
         * 步骤：显示路线、日期、查询时间
         */
        fun bind(history: QueryHistoryEntity) {
            binding.tvRoute.text = "${history.fromStationName} → ${history.toStationName}"
            binding.tvDate.text = history.trainDate
            binding.tvQueryTime.text = getRelativeTime(history.queryTime)
        }

        private fun getRelativeTime(timestamp: Long): String {
            val diff = System.currentTimeMillis() - timestamp
            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "刚刚"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}分钟前"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)}小时前"
                diff < TimeUnit.DAYS.toMillis(7) -> "${TimeUnit.MILLISECONDS.toDays(diff)}天前"
                else -> {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.format(Date(timestamp))
                }
            }
        }
    }

    companion object QueryHistoryDiffCallback : DiffUtil.ItemCallback<QueryHistoryEntity>() {
        override fun areItemsTheSame(oldItem: QueryHistoryEntity, newItem: QueryHistoryEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: QueryHistoryEntity, newItem: QueryHistoryEntity): Boolean {
            return oldItem == newItem
        }
    }
}
