package com.ticket12306.android

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ticket12306.android.booking.BookingManager
import com.ticket12306.android.booking.TicketMonitor
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.LogType
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * 余票检查Worker（兼容旧版）
 * 使用BookingWorker替代，此Worker保留用于向后兼容
 */
@HiltWorker
class TicketCheckWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val ticketRepository: TicketRepository,
    private val notificationHelper: NotificationHelper,
    private val bookingTaskDao: BookingTaskDao,
    private val bookingLogDao: BookingLogDao,
    private val ticketMonitor: TicketMonitor,
    private val bookingManager: BookingManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val bookingTasks = ticketRepository.getActiveBookingTasks()

            for (task in bookingTasks) {
                val result = ticketRepository.checkTicketAvailability(task)

                bookingLogDao.insertLog(
                    BookingLog(
                        taskId = task.id,
                        type = LogType.QUERY.name,
                        message = "Worker查询: ${task.trainNumber} - ${if (result.hasTicket) "有余票" else "无票"}"
                    )
                )

                if (result.hasTicket) {
                    notificationHelper.showTicketAvailableNotification(
                        task.trainNumber,
                        task.departureDate
                    )

                    if (task.autoBooking) {
                        bookingTaskDao.updateTaskStatus(task.id, BookingStatus.BOOKING.name)
                        val bookingResult = bookingManager.executeBooking(task, result)
                        if (bookingResult.success) {
                            notificationHelper.showBookingSuccessNotification(
                                task.trainNumber,
                                task.departureDate
                            )
                        } else {
                            notificationHelper.showBookingFailedNotification(
                                task.trainNumber,
                                bookingResult.errorMessage ?: "下单失败"
                            )
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
