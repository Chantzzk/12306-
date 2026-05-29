package com.ticket12306.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ticket12306.android.data.model.BookingLog
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingLogDao {

    @Query("SELECT * FROM booking_logs WHERE taskId = :taskId ORDER BY timestamp DESC")
    fun getLogsByTaskId(taskId: Long): Flow<List<BookingLog>>

    @Query("SELECT * FROM booking_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<BookingLog>>

    @Query("SELECT * FROM booking_logs WHERE taskId = :taskId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentLogsByTaskId(taskId: Long, limit: Int = 50): List<BookingLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BookingLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<BookingLog>)

    @Query("DELETE FROM booking_logs WHERE taskId = :taskId")
    suspend fun deleteLogsByTaskId(taskId: Long)

    @Query("DELETE FROM booking_logs WHERE timestamp < :beforeTimestamp")
    suspend fun deleteLogsBefore(beforeTimestamp: Long): Int

    @Query("SELECT COUNT(*) FROM booking_logs WHERE taskId = :taskId")
    suspend fun getLogCountByTaskId(taskId: Long): Int
}
