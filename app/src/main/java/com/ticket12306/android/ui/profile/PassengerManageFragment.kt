package com.ticket12306.android.ui.profile

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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ticket12306.android.R
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.databinding.FragmentPassengerManageBinding
import com.ticket12306.android.databinding.ItemPassengerManageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PassengerManageFragment : Fragment() {

    private var _binding: FragmentPassengerManageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PassengerManageViewModel by viewModels()
    private lateinit var passengerAdapter: PassengerManageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassengerManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeState()
    }

    /**
     * 初始化乘客列表RecyclerView
     * 步骤：
     * 1. 创建PassengerManageAdapter，设置删除按钮点击事件
     * 2. 配置LinearLayoutManager
     */
    private fun setupRecyclerView() {
        passengerAdapter = PassengerManageAdapter { passenger ->
            showDeleteConfirmDialog(passenger)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = passengerAdapter
        }
    }

    /**
     * 设置下拉刷新
     */
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshPassengers()
        }
    }

    /**
     * 显示删除乘客确认对话框
     * 步骤：
     * 1. 构建确认对话框
     * 2. 用户确认后调用ViewModel删除乘客
     */
    private fun showDeleteConfirmDialog(passenger: Passenger) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_passenger))
            .setMessage("确定要删除乘客 ${passenger.passenger_name} 吗？")
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                viewModel.deletePassenger(passenger)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * 观察ViewModel状态变化
     */
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    passengerAdapter.submitList(state.passengers)
                    binding.emptyView.visibility =
                        if (state.passengers.isEmpty()) View.VISIBLE else View.GONE
                    binding.swipeRefresh.isRefreshing = state.isRefreshing
                }
            }
        }

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

class PassengerManageAdapter(
    private val onDeleteClick: (Passenger) -> Unit
) : ListAdapter<Passenger, PassengerManageAdapter.ViewHolder>(PassengerDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPassengerManageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemPassengerManageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(passenger: Passenger) {
            binding.tvPassengerName.text = passenger.passenger_name
            binding.tvPassengerId.text = "${passenger.passenger_id_type_name}: ${maskIdNumber(passenger.passenger_id_no)}"
            binding.tvPassengerType.text = passenger.sex_name

            binding.ivDelete.setOnClickListener {
                onDeleteClick(passenger)
            }
        }

        private fun maskIdNumber(idNo: String): String {
            if (idNo.length > 6) {
                return idNo.substring(0, 3) + "***********" + idNo.substring(idNo.length - 4)
            }
            return idNo
        }
    }

    companion object PassengerDiffCallback : DiffUtil.ItemCallback<Passenger>() {
        override fun areItemsTheSame(oldItem: Passenger, newItem: Passenger): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: Passenger, newItem: Passenger): Boolean {
            return oldItem == newItem
        }
    }
}
