package com.ticket12306.android

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.ticket12306.android.booking.BookingStateManager
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.remote.RetrofitClient
import com.ticket12306.android.util.NotificationHelper
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltAndroidApp
class TicketApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var bookingStateManager: BookingStateManager

    @Inject
    lateinit var userPreferences: UserPreferences

    private val applicationScope = CoroutineScope(Dispatchers.IO + Job())

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        installCrashHandler()
        RetrofitClient.init(userPreferences)
        restoreTokenCache()
        createNotificationChannels()
        restoreActiveTasks()
    }

    override fun onTerminate() {
        super.onTerminate()
        applicationScope.cancel()
        bookingStateManager.destroy()
    }

    private fun installCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            writeCrashToFile(thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun writeCrashToFile(thread: Thread, throwable: Throwable) {
        try {
            val dir = File(getExternalFilesDir(null), "crash_logs")
            if (!dir.exists()) dir.mkdirs()
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(dir, "crash_${timestamp}.txt")
            PrintWriter(FileWriter(file)).use { writer ->
                writer.println("=== Crash Log ===")
                writer.println("Time: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())}")
                writer.println("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                writer.println("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                writer.println("App: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                writer.println("Thread: ${thread.name}")
                writer.println()
                writer.println("--- Exception ---")
                throwable.printStackTrace(writer)
                writer.println()
                var cause = throwable.cause
                var depth = 1
                while (cause != null && depth <= 5) {
                    writer.println("--- Cause $depth ---")
                    cause.printStackTrace(writer)
                    writer.println()
                    cause = cause.cause
                    depth++
                }
                writer.flush()
            }
        } catch (_: Exception) {
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val bookingChannel = NotificationChannel(
                NotificationHelper.CHANNEL_BOOKING,
                "抢票通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "抢票状态和结果通知"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(bookingChannel)

            val statusChannel = NotificationChannel(
                NotificationHelper.CHANNEL_STATUS,
                "状态通知",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "应用状态通知"
            }
            notificationManager.createNotificationChannel(statusChannel)
        }
    }

    private fun restoreActiveTasks() {
        applicationScope.launch {
            try {
                bookingStateManager.startAllActiveTasks()
            } catch (_: Exception) {
            }
        }
    }

    private fun restoreTokenCache() {
        applicationScope.launch {
            try {
                val token = userPreferences.token.first()
                RetrofitClient.updateToken(token)
            } catch (_: Exception) {
            }
        }
    }

    companion object {
        fun getCrashLogDir(context: Context): File {
            return File(context.getExternalFilesDir(null), "crash_logs")
        }
    }
}
