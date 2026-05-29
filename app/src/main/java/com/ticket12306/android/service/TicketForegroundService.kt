package com.ticket12306.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.util.NotificationHelper
import com.ticket12306.android.util.NotificationHelper.Companion.ACTION_STOP_SERVICE
import com.ticket12306.android.util.NotificationHelper.Companion.EXTRA_TASK_ID
import com.ticket12306.android.util.NotificationHelper.Companion.NOTIFICATION_ID_PROGRESS_PREFIX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 抢票前台服务
 * 在后台持续执行抢票任务，通过前台通知显示实时进度
 *
 * 启动流程：
 * 1. 外部通过startService启动，传入BookingTask数据
 * 2. 服务创建前台通知，进入前台运行状态
 * 3. 按固定间隔查询余票并尝试下单
 * 4. 抢票成功/失败/手动停止时，停止服务并清除通知
 */
@AndroidEntryPoint
class TicketForegroundService : Service() {

    @Inject
    lateinit var ticketRepository: TicketRepository

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var bookingJob: Job? = null

    /**
     * 当前正在运行的抢票任务
     */
    private val _currentTask = MutableStateFlow<BookingTask?>(null)
    val currentTask: StateFlow<BookingTask?> = _currentTask

    /**
     * 已查询次数
     */
    private var queryCount = 0

    /**
     * 任务开始时间戳
     */
    private var startTimeMs: Long = 0L

    /**
     * 查询间隔（毫秒），默认5秒
     */
    private var queryIntervalMs: Long = DEFAULT_QUERY_INTERVAL_MS

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SERVICE -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
                stopBookingTask(taskId)
                return START_NOT_STICKY
            }
        }

        val task = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_BOOKING_TASK, BookingTask::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_BOOKING_TASK)
        }

        if (task == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        startBookingTask(task)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        bookingJob?.cancel()
        serviceScope.cancel()
    }

    /**
     * 启动抢票任务
     * 1. 记录任务信息和起始时间
     * 2. 创建并显示前台通知
     * 3. 启动查询循环
     */
    private fun startBookingTask(task: BookingTask) {
        _currentTask.value = task
        queryCount = 0
        startTimeMs = System.currentTimeMillis()

        val notificationId = NOTIFICATION_ID_PROGRESS_PREFIX + task.id.toInt()
        val initialNotification = notificationHelper.buildProgressNotification(
            taskId = task.id,
            trainNumber = task.trainNumber,
            queryCount = 0,
            elapsedSeconds = 0
        )

        startForeground(notificationId, initialNotification)

        bookingJob = serviceScope.launch {
            while (isActive) {
                val elapsedSeconds = (System.currentTimeMillis() - startTimeMs) / 1000

                try {
                    queryCount++
                    val result = ticketRepository.checkTicketAvailability(task)

                    if (result.hasTicket) {
                        if (task.autoBooking) {
                            val bookingResult = ticketRepository.bookTicket(task)
                            if (bookingResult.success) {
                                onBookingSuccess(task)
                            } else {
                                onBookingFailed(task, bookingResult.errorMessage ?: "预订失败")
                            }
                        } else {
                            onBookingSuccess(task)
                        }
                        break
                    }

                    updateProgressNotification(task, queryCount, elapsedSeconds)
                } catch (e: Exception) {
                    updateProgressNotification(task, queryCount, elapsedSeconds)
                }

                delay(queryIntervalMs)
            }
        }
    }

    /**
     * 更新抢票进度通知
     * 使用频率控制，每5秒最多更新一次
     */
    private fun updateProgressNotification(task: BookingTask, count: Int, elapsedSeconds: Long) {
        val notificationId = NOTIFICATION_ID_PROGRESS_PREFIX + task.id.toInt()
        val notification = notificationHelper.buildProgressNotification(
            taskId = task.id,
            trainNumber = task.trainNumber,
            queryCount = count,
            elapsedSeconds = elapsedSeconds
        )
        notificationHelper.updateProgressNotification(notificationId, notification)
    }

    /**
     * 抢票成功处理
     * 1. 显示成功通知
     * 2. 停止前台服务
     */
    private fun onBookingSuccess(task: BookingTask) {
        notificationHelper.showSuccessNotification(
            trainNumber = task.trainNumber,
            seatTypeName = task.seatTypeName,
            passengerNames = task.passengerNames,
            taskId = task.id
        )
        stopBookingTask(task.id)
    }

    /**
     * 抢票失败处理
     * 1. 显示失败通知
     * 2. 停止前台服务
     */
    private fun onBookingFailed(task: BookingTask, reason: String) {
        notificationHelper.showFailedNotification(
            trainNumber = task.trainNumber,
            reason = reason,
            taskId = task.id
        )
        stopBookingTask(task.id)
    }

    /**
     * 停止抢票任务
     * 取消协程、清除通知、停止前台服务
     */
    private fun stopBookingTask(taskId: Long) {
        bookingJob?.cancel()
        bookingJob = null
        _currentTask.value = null

        val notificationId = NOTIFICATION_ID_PROGRESS_PREFIX + taskId.toInt()
        notificationHelper.cancelNotification(notificationId)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    companion object {
        const val EXTRA_BOOKING_TASK = "extra_booking_task"
        private const val DEFAULT_QUERY_INTERVAL_MS = 5_000L

        /**
         * 构建启动服务的Intent
         */
        fun createStartIntent(
            context: android.content.Context,
            task: BookingTask
        ): Intent {
            return Intent(context, TicketForegroundService::class.java).apply {
                putExtra(EXTRA_BOOKING_TASK, task)
            }
        }

        /**
         * 构建停止服务的Intent
         */
        fun createStopIntent(
            context: android.content.Context,
            taskId: Long
        ): Intent {
            return Intent(context, TicketForegroundService::class.java).apply {
                action = ACTION_STOP_SERVICE
                putExtra(EXTRA_TASK_ID, taskId)
            }
        }
    }

}
