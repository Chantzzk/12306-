package com.ticket12306.android.booking

import com.ticket12306.android.booking.strategy.BookingStrategyFactory
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingResult
import com.ticket12306.android.data.model.BookingStrategyType
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.LogType
import com.ticket12306.android.data.model.TicketCheckResult
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 自动下单管理器
 * 核心职责：
 * 1. 完整下单流程：检查余票 → 提交订单 → 确认订单
 * 2. 订单提交失败处理
 * 3. 并发控制（防止重复下单）
 * 4. 下单超时处理
 * 5. 重试机制（指数退避）
 */
@Singleton
class BookingManager @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val bookingTaskDao: BookingTaskDao,
    private val bookingLogDao: BookingLogDao,
    private val notificationHelper: NotificationHelper,
    private val ticketMonitor: TicketMonitor
) {

    private val bookingMutex = Mutex()
    private val activeBookingTasks = mutableSetOf<Long>()
    private val bookingJobs = mutableMapOf<Long, Job>()

    private val _bookingResults = MutableStateFlow<Map<Long, BookingResult>>(emptyMap())
    val bookingResults: StateFlow<Map<Long, BookingResult>> = _bookingResults.asStateFlow()

    private val retryIntervals = listOf(3_000L, 5_000L, 10_000L, 30_000L, 60_000L)

    /**
     * 执行自动下单流程
     * 步骤：
     * 1. 并发控制检查，防止重复下单
     * 2. 检查余票是否满足条件
     * 3. 如果有座次偏好，尝试偏好座次
     * 4. 提交订单
     * 5. 确认订单
     * 6. 处理下单结果
     * 7. 失败时进入重试流程
     */
    suspend fun executeBooking(task: BookingTask, checkResult: TicketCheckResult): BookingResult {
        bookingMutex.withLock {
            if (activeBookingTasks.contains(task.id)) {
                logResult(task.id, LogType.BOOKING, "该任务正在下单中，跳过重复请求")
                return BookingResult(success = false, errorMessage = "正在下单中")
            }
            activeBookingTasks.add(task.id)
        }

        try {
            val strategy = BookingStrategyFactory.getStrategy(
                BookingStrategyType.valueOf(task.strategy)
            )

            if (!strategy.shouldBookNow(checkResult, task)) {
                return BookingResult(success = false, errorMessage = "不满足抢票条件")
            }

            logResult(task.id, LogType.BOOKING, "开始下单: ${task.trainNumber} ${task.seatTypeName}")
            bookingTaskDao.updateTaskStatus(task.id, com.ticket12306.android.data.model.BookingStatus.BOOKING.name)

            val effectiveTask = resolveEffectiveTask(task, checkResult)

            val result = performBookingWithTimeout(effectiveTask, strategy.getBookingTimeout())

            _bookingResults.value = _bookingResults.value.toMutableMap().apply {
                this[task.id] = result
            }

            handleBookingResult(task, result)

            return result
        } finally {
            bookingMutex.withLock {
                activeBookingTasks.remove(task.id)
            }
        }
    }

    /**
     * 解析实际下单使用的任务配置
     * 步骤：
     * 1. 如果有座次偏好且当前座次无票，尝试偏好座次中的有票座次
     * 2. 否则使用原任务配置
     */
    private suspend fun resolveEffectiveTask(task: BookingTask, checkResult: TicketCheckResult): BookingTask {
        if (task.seatPreferences.isNotEmpty() && !checkResult.hasTicket) {
            val preferredResult = ticketMonitor.checkPreferredSeats(task)
            if (preferredResult != null && preferredResult.hasTicket) {
                val bestSeat = preferredResult.seatInfo
                if (bestSeat != null) {
                    logResult(
                        task.id, LogType.BOOKING,
                        "偏好座次发现余票: ${bestSeat.seatTypeName} 剩余${bestSeat.remainTicket}张"
                    )
                    return task.copy(
                        seatType = bestSeat.seatType,
                        seatTypeName = bestSeat.seatTypeName
                    )
                }
            }
        }
        return task
    }

    /**
     * 带超时的下单执行
     * 步骤：
     * 1. 调用Repository下单
     * 2. 使用withTimeout控制超时
     * 3. 超时返回失败结果
     */
    private suspend fun performBookingWithTimeout(task: BookingTask, timeoutMs: Long): BookingResult {
        return try {
            kotlinx.coroutines.withTimeout(timeoutMs) {
                ticketRepository.bookTicket(task)
            }
        } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
            logResult(task.id, LogType.ERROR, "下单超时（${timeoutMs / 1000}秒）")
            BookingResult(success = false, errorMessage = "下单超时")
        }
    }

    /**
     * 处理下单结果
     * 步骤：
     * 1. 成功：更新状态、发送通知
     * 2. 失败：记录日志、触发重试
     */
    private suspend fun handleBookingResult(task: BookingTask, result: BookingResult) {
        if (result.success) {
            bookingTaskDao.updateTaskStatus(task.id, com.ticket12306.android.data.model.BookingStatus.SUCCESS.name)
            bookingTaskDao.deactivateBookingTask(task.id)
            notificationHelper.showBookingSuccessNotification(task.trainNumber, task.departureDate)
            logResult(task.id, LogType.BOOKING, "抢票成功！订单号: ${result.orderSequence}")
        } else {
            logResult(task.id, LogType.ERROR, "下单失败: ${result.errorMessage}")
            handleBookingFailure(task, result.errorMessage ?: "未知错误")
        }
    }

    /**
     * 处理下单失败，启动重试流程
     * 步骤：
     * 1. 检查重试次数是否超过上限
     * 2. 计算重试间隔（指数退避）
     * 3. 更新重试计数
     * 4. 延迟后重新触发监控
     */
    private suspend fun handleBookingFailure(task: BookingTask, errorMessage: String) {
        val currentRetry = task.currentRetryCount + 1

        if (currentRetry >= task.maxRetryCount) {
            bookingTaskDao.updateTaskStatus(task.id, com.ticket12306.android.data.model.BookingStatus.FAILED.name)
            bookingTaskDao.deactivateBookingTask(task.id)
            notificationHelper.showBookingFailedNotification(task.trainNumber, "已达最大重试次数")
            logResult(task.id, LogType.ERROR, "已达最大重试次数(${task.maxRetryCount})，停止抢票")
            return
        }

        bookingTaskDao.updateRetryCount(task.id, currentRetry)

        val retryInterval = calculateRetryInterval(currentRetry)
        logResult(
            task.id, LogType.RETRY,
            "第${currentRetry}次重试，${retryInterval / 1000}秒后重试（原因: $errorMessage）"
        )

        if (isServerBusy(errorMessage)) {
            logResult(task.id, LogType.STRATEGY, "服务器繁忙，自动降速")
            delay(retryInterval * 2)
        } else {
            delay(retryInterval)
        }

        bookingTaskDao.updateTaskStatus(task.id, com.ticket12306.android.data.model.BookingStatus.MONITORING.name)
    }

    /**
     * 计算重试间隔（指数退避）
     * 步骤：
     * 1. 根据重试次数从间隔列表中取值
     * 2. 超出列表范围则使用最大间隔
     */
    private fun calculateRetryInterval(retryCount: Int): Long {
        val index = (retryCount - 1).coerceAtMost(retryIntervals.size - 1)
        return retryIntervals[index]
    }

    /**
     * 判断是否为服务器繁忙
     * 步骤：检查错误信息中是否包含繁忙相关关键词
     */
    private fun isServerBusy(errorMessage: String): Boolean {
        val busyKeywords = listOf("繁忙", "请求过多", "too many", "busy", "排队", "队列")
        return busyKeywords.any { errorMessage.contains(it, ignoreCase = true) }
    }

    /**
     * 启动自动抢票循环（完整流程：监控+下单）
     * 步骤：
     * 1. 更新任务状态为监控中
     * 2. 启动余票监控
     * 3. 有票时自动触发下单
     * 4. 下单失败自动重试
     */
    fun startAutoBooking(scope: CoroutineScope, task: BookingTask) {
        stopAutoBooking(task.id)

        val job = scope.launch {
            bookingTaskDao.updateTaskStatus(task.id, com.ticket12306.android.data.model.BookingStatus.MONITORING.name)

            ticketMonitor.startMonitoring(scope, task) { checkResult ->
                launch {
                    val result = executeBooking(task, checkResult)
                    if (!result.success && task.isActive) {
                        bookingTaskDao.updateTaskStatus(
                            task.id,
                            com.ticket12306.android.data.model.BookingStatus.MONITORING.name
                        )
                    }
                }
            }
        }

        bookingJobs[task.id] = job
    }

    /** 停止自动抢票 */
    fun stopAutoBooking(taskId: Long) {
        ticketMonitor.stopMonitoring(taskId)
        bookingJobs[taskId]?.cancel()
        bookingJobs.remove(taskId)
    }

    /** 停止所有自动抢票 */
    fun stopAllAutoBooking() {
        ticketMonitor.stopAllMonitoring()
        bookingJobs.values.forEach { it.cancel() }
        bookingJobs.clear()
    }

    /** 检查任务是否正在抢票中 */
    fun isBooking(taskId: Long): Boolean {
        return activeBookingTasks.contains(taskId) || bookingJobs[taskId]?.isActive == true
    }

    private suspend fun logResult(taskId: Long, type: LogType, message: String) {
        bookingLogDao.insertLog(
            BookingLog(taskId = taskId, type = type.name, message = message)
        )
    }
}
