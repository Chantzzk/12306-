package com.ticket12306.android.booking

import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.LogType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 抢票状态管理器
 * 核心职责：
 * 1. 管理抢票任务的状态转换（等待中→监控中→抢票中→成功/失败/已取消）
 * 2. 状态持久化到数据库
 * 3. 状态变更通知（Flow）
 * 4. 抢票任务列表管理
 */
@Singleton
class BookingStateManager @Inject constructor(
    private val bookingTaskDao: BookingTaskDao,
    private val bookingLogDao: BookingLogDao,
    private val bookingManager: BookingManager
) {

    private val _taskStates = MutableStateFlow<Map<Long, BookingStatus>>(emptyMap())
    val taskStates: StateFlow<Map<Long, BookingStatus>> = _taskStates.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    /**
     * 释放资源，取消所有协程
     * 步骤：
     * 1. 停止所有自动抢票
     * 2. 取消协程作用域
     * 3. 清空状态缓存
     */
    fun destroy() {
        stopAllTasks()
        scope.cancel()
    }

    /**
     * 启动抢票任务
     * 步骤：
     * 1. 验证任务是否存在
     * 2. 激活任务
     * 3. 更新任务状态为监控中
     * 4. 启动自动抢票
     * 5. 记录日志
     */
    suspend fun startTask(task: BookingTask) {
        val existingTask = bookingTaskDao.getBookingTaskById(task.id)
        if (existingTask == null) {
            logState(task.id, "任务不存在")
            return
        }

        bookingTaskDao.activateBookingTask(task.id)
        updateTaskState(task.id, BookingStatus.MONITORING)
        bookingManager.startAutoBooking(scope, existingTask)
        logState(task.id, "抢票任务已启动: ${task.trainNumber}")
    }

    /**
     * 停止抢票任务
     * 步骤：
     * 1. 停止自动抢票
     * 2. 更新任务状态为已取消
     * 3. 停用任务
     * 4. 记录日志
     */
    suspend fun stopTask(taskId: Long) {
        bookingManager.stopAutoBooking(taskId)
        bookingTaskDao.deactivateBookingTask(taskId)
        updateTaskState(taskId, BookingStatus.CANCELLED)
        logState(taskId, "抢票任务已停止")
    }

    /**
     * 重置抢票任务（重新开始）
     * 步骤：
     * 1. 停止当前抢票
     * 2. 重置任务状态和重试计数
     * 3. 记录日志
     */
    suspend fun resetTask(taskId: Long) {
        bookingManager.stopAutoBooking(taskId)
        bookingTaskDao.resetTask(taskId)
        updateTaskState(taskId, BookingStatus.PENDING)
        logState(taskId, "抢票任务已重置")
    }

    /**
     * 删除抢票任务
     * 步骤：
     * 1. 停止抢票
     * 2. 删除任务
     * 3. 移除状态缓存
     */
    suspend fun deleteTask(taskId: Long) {
        bookingManager.stopAutoBooking(taskId)
        bookingTaskDao.deleteBookingTaskById(taskId)
        _taskStates.value = _taskStates.value.toMutableMap().apply { remove(taskId) }
    }

    /**
     * 获取任务当前状态
     * 步骤：
     * 1. 优先从内存缓存获取
     * 2. 缓存未命中则从数据库读取
     */
    suspend fun getTaskStatus(taskId: Long): BookingStatus {
        val cached = _taskStates.value[taskId]
        if (cached != null) return cached

        val task = bookingTaskDao.getBookingTaskById(taskId)
        val status = if (task != null) {
            try {
                BookingStatus.valueOf(task.status)
            } catch (e: IllegalArgumentException) {
                BookingStatus.PENDING
            }
        } else {
            BookingStatus.CANCELLED
        }

        _taskStates.value = _taskStates.value.toMutableMap().apply { this[taskId] = status }
        return status
    }

    /**
     * 批量启动所有活跃任务
     * 步骤：
     * 1. 查询所有活跃任务
     * 2. 逐个启动
     */
    suspend fun startAllActiveTasks() {
        val tasks = bookingTaskDao.getActiveBookingTasks().first()
        tasks.forEach { task ->
            startTask(task)
        }
    }

    /**
     * 停止所有任务
     * 步骤：
     * 1. 停止所有自动抢票
     * 2. 清空状态缓存
     */
    fun stopAllTasks() {
        bookingManager.stopAllAutoBooking()
        _taskStates.value = emptyMap()
    }

    /**
     * 更新任务状态
     * 步骤：
     * 1. 更新内存缓存
     * 2. 持久化到数据库
     */
    private suspend fun updateTaskState(taskId: Long, status: BookingStatus) {
        _taskStates.value = _taskStates.value.toMutableMap().apply {
            this[taskId] = status
        }
        bookingTaskDao.updateTaskStatus(taskId, status.name)
    }

    private suspend fun logState(taskId: Long, message: String) {
        bookingLogDao.insertLog(
            BookingLog(taskId = taskId, type = LogType.STATUS_CHANGE.name, message = message)
        )
    }
}
