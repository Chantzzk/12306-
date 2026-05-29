package com.ticket12306.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_logs")
data class BookingLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String,
    val message: String,
    val extraData: String? = null
)

enum class LogType {
    QUERY,
    BOOKING,
    STATUS_CHANGE,
    ERROR,
    RETRY,
    STRATEGY
}
