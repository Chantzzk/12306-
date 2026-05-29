package com.ticket12306.android.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "booking_tasks")
@Parcelize
data class BookingTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val trainNumber: String,
    val trainNo: String,
    val departureStation: String,
    val departureStationName: String,
    val arrivalStation: String,
    val arrivalStationName: String,
    val departureDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val seatType: String,
    val seatTypeName: String,
    val passengerIds: List<String>,
    val passengerNames: List<String>,
    val autoBooking: Boolean = true,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val strategy: String = BookingStrategyType.NORMAL.name,
    val refreshInterval: Int = 5,
    val maxRetryCount: Int = 50,
    val seatPreferences: List<String> = emptyList(),
    val acceptWaitlist: Boolean = false,
    val currentRetryCount: Int = 0,
    val status: String = BookingStatus.PENDING.name
) : Parcelable

data class BookingTaskWithStatus(
    val task: BookingTask,
    val status: BookingStatus
)

enum class BookingStatus {
    PENDING,
    MONITORING,
    BOOKING,
    SUCCESS,
    FAILED,
    CANCELLED
}

enum class BookingStrategyType {
    NORMAL,
    HIGH_SPEED,
    EXTREME,
    SMART
}
