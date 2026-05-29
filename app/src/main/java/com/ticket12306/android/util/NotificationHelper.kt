package com.ticket12306.android.util

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.ticket12306.android.R
import com.ticket12306.android.service.TicketForegroundService
import com.ticket12306.android.ui.main.MainActivity

class NotificationHelper(private val context: Context) {

    fun showTicketAvailableNotification(trainNumber: String, date: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("有余票")
            .setContentText("$trainNumber $date 有余票，请尽快预订")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_TICKET, notification)
    }

    fun showBookingSuccessNotification(trainNumber: String, date: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票成功")
            .setContentText("$trainNumber $date 抢票成功，请及时支付")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_BOOKING, notification)
    }

    fun showBookingFailedNotification(trainNumber: String, reason: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票失败")
            .setContentText("$trainNumber 抢票失败: $reason")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_BOOKING, notification)
    }

    /**
     * 显示抢票状态变更通知
     * 步骤：
     * 1. 构建通知内容
     * 2. 使用状态通道发送低优先级通知
     */
    fun showBookingStatusNotification(trainNumber: String, status: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_STATUS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票状态更新")
            .setContentText("$trainNumber $status")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_STATUS, notification)
    }

    /**
     * 显示重试通知
     */
    fun showRetryNotification(trainNumber: String, retryCount: Int, maxRetry: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_STATUS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票重试中")
            .setContentText("$trainNumber 第${retryCount}/${maxRetry}次重试")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_STATUS, notification)
    }

    /**
     * 构建抢票进度通知
     * 步骤：
     * 1. 创建点击跳转到主页的PendingIntent
     * 2. 创建停止抢票的Action PendingIntent
     * 3. 构建进度通知，显示查询次数和耗时
     */
    fun buildProgressNotification(
        taskId: Long,
        trainNumber: String,
        queryCount: Int,
        elapsedSeconds: Long
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                action = ACTION_VIEW_BOOKING_DETAIL
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            context,
            taskId.toInt(),
            TicketForegroundService.createStopIntent(context, taskId),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val elapsedMin = elapsedSeconds / 60
        val elapsedSec = elapsedSeconds % 60
        val timeText = if (elapsedMin > 0) "${elapsedMin}分${elapsedSec}秒" else "${elapsedSec}秒"

        return NotificationCompat.Builder(context, CHANNEL_STATUS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票监控中: $trainNumber")
            .setContentText("已查询${queryCount}次，耗时$timeText")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_launcher_foreground, "停止", stopIntent)
            .build()
    }

    /**
     * 更新抢票进度通知
     */
    fun updateProgressNotification(notificationId: Int, notification: Notification) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }

    /**
     * 显示抢票成功通知（前台服务使用）
     * 步骤：构建包含车次、座次、乘客信息的通知
     */
    fun showSuccessNotification(
        trainNumber: String,
        seatTypeName: String,
        passengerNames: List<String>,
        taskId: Long
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = ACTION_VIEW_ORDER_DETAIL
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val passengerText = passengerNames.joinToString("、")
        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票成功！$trainNumber")
            .setContentText("$seatTypeName - $passengerText，请及时支付")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_BOOKING, notification)
    }

    /**
     * 显示抢票失败通知（前台服务使用）
     */
    fun showFailedNotification(trainNumber: String, reason: String, taskId: Long) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = ACTION_VIEW_BOOKING_DETAIL
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_BOOKING)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("抢票失败: $trainNumber")
            .setContentText(reason)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_BOOKING, notification)
    }

    /**
     * 取消指定通知
     */
    fun cancelNotification(notificationId: Int) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(notificationId)
    }

    companion object {
        const val CHANNEL_BOOKING = "channel_booking"
        const val CHANNEL_STATUS = "channel_status"

        const val ACTION_VIEW_ORDER_DETAIL = "com.ticket12306.android.ACTION_VIEW_ORDER_DETAIL"
        const val ACTION_VIEW_BOOKING_DETAIL = "com.ticket12306.android.ACTION_VIEW_BOOKING_DETAIL"
        const val ACTION_STOP_SERVICE = "com.ticket12306.android.ACTION_STOP_SERVICE"

        const val EXTRA_TASK_ID = "extra_task_id"

        const val NOTIFICATION_ID_PROGRESS_PREFIX = 2000

        private const val NOTIFICATION_ID_TICKET = 1001
        private const val NOTIFICATION_ID_BOOKING = 1002
        private const val NOTIFICATION_ID_STATUS = 1003
    }
}
