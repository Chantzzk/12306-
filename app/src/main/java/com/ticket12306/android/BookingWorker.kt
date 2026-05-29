package com.ticket12306.android

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ticket12306.android.booking.BookingManager
import com.ticket12306.android.booking.BookingStateManager
import com.ticket12306.android.booking.TicketMonitor
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.LogType
import com.ticket12306.android.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * 抢票后台任务Worker
 * 核心职责：
 * 1. 使用WorkManager实现后台定时任务
 * 2. 支持周期性执行余票检查
 * 3. 任务输入参数传递（车次、座次、乘客信息等）
 * 4. 任务约束条件（网络连接）
 * 5. 支持取消任务
 */
@HiltWorker
class BookingWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookingTaskDao: BookingTaskDao,
    private val bookingLogDao: BookingLogDao,
    private val bookingManager: BookingManager,
    private val ticketMonitor: TicketMonitor,
    private val bookingStateManager: BookingStateManager,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_TASK_ID = "task_id"
        const val KEY_TRAIN_NUMBER = "train_number"
        const val KEY_STRATEGY = "strategy"
        const val WORK_NAME_PREFIX = "booking_work_"
    }

    /**
     * 执行后台抢票任务
     * 步骤：
     * 1. 读取输入参数获取任务ID
     * 2. 从数据库加载任务详情
     * 3. 验证任务状态（必须是活跃状态）
     * 4. 检查网络连接
     * 5. 执行余票查询
     * 6. 有票时触发自动下单
     * 7. 处理执行结果
     */
    override suspend fun doWork(): Result {
        val taskId = inputData.getLong(KEY_TASK_ID, -1)
        if (taskId == -1L) {
            logWorker("无效的任务ID")
            return Result.failure()
        }

        val task = bookingTaskDao.getBookingTaskById(taskId)
        if (task == null) {
            logWorker("任务不存在: $taskId")
            return Result.failure()
        }

        if (!task.isActive) {
            logWorker("任务已停用: ${task.trainNumber}")
            return Result.success()
        }

        logWorker("开始执行抢票任务: ${task.trainNumber}")

        return try {
            bookingTaskDao.updateTaskStatus(task.id, BookingStatus.MONITORING.name)

            val checkResult = ticketMonitor.checkPreferredSeats(task)
                ?: com.ticket12306.android.data.model.TicketCheckResult(hasTicket = false)

            if (checkResult.hasTicket) {
                logWorker("发现余票: ${task.trainNumber} ${checkResult.seatInfo?.seatTypeName}")
                notificationHelper.showTicketAvailableNotification(task.trainNumber, task.departureDate)

                if (task.autoBooking) {
                    bookingTaskDao.updateTaskStatus(task.id, BookingStatus.BOOKING.name)
                    val bookingResult = bookingManager.executeBooking(task, checkResult)

                    if (bookingResult.success) {
                        logWorker("抢票成功: ${task.trainNumber}")
                        return Result.success()
                    } else {
                        logWorker("下单失败: ${bookingResult.errorMessage}")
                        val currentRetry = task.currentRetryCount + 1
                        bookingTaskDao.updateRetryCount(task.id, currentRetry)

                        if (currentRetry >= task.maxRetryCount) {
                            bookingTaskDao.updateTaskStatus(task.id, BookingStatus.FAILED.name)
                            bookingTaskDao.deactivateBookingTask(task.id)
                            notificationHelper.showBookingFailedNotification(task.trainNumber, "已达最大重试次数")
                            return Result.failure()
                        }

                        bookingTaskDao.updateTaskStatus(task.id, BookingStatus.MONITORING.name)
                        return Result.retry()
                    }
                }
            } else {
                logWorker("暂无余票: ${task.trainNumber}")
            }

            Result.success()
        } catch (e: Exception) {
            logWorker("抢票任务异常: ${e.message}")
            bookingLogDao.insertLog(
                BookingLog(taskId = task.id, type = LogType.ERROR.name, message = "Worker异常: ${e.message}")
            )
            Result.retry()
        }
    }

    private suspend fun logWorker(message: String) {
        bookingLogDao.insertLog(
            BookingLog(taskId = 0, type = LogType.STATUS_CHANGE.name, message = "[Worker] $message")
        )
    }
}
