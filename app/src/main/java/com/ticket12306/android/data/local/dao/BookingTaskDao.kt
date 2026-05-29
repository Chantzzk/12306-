package com.ticket12306.android.data.local.dao

import androidx.room.*
import com.ticket12306.android.data.model.BookingTask
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingTaskDao {

    @Query("SELECT * FROM booking_tasks ORDER BY createdAt DESC")
    fun getAllBookingTasks(): Flow<List<BookingTask>>

    @Query("SELECT * FROM booking_tasks WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveBookingTasks(): Flow<List<BookingTask>>

    @Query("SELECT * FROM booking_tasks WHERE id = :id")
    suspend fun getBookingTaskById(id: Long): BookingTask?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookingTask(task: BookingTask): Long

    @Update
    suspend fun updateBookingTask(task: BookingTask)

    @Delete
    suspend fun deleteBookingTask(task: BookingTask)

    @Query("DELETE FROM booking_tasks WHERE id = :id")
    suspend fun deleteBookingTaskById(id: Long)

    @Query("UPDATE booking_tasks SET isActive = 0 WHERE id = :id")
    suspend fun deactivateBookingTask(id: Long)

    @Query("UPDATE booking_tasks SET isActive = 1 WHERE id = :id")
    suspend fun activateBookingTask(id: Long)

    @Query("DELETE FROM booking_tasks WHERE isActive = 0")
    suspend fun deleteInactiveBookingTasks()

    @Query("SELECT COUNT(*) FROM booking_tasks WHERE isActive = 1")
    fun getActiveTaskCount(): Flow<Int>

    @Query("UPDATE booking_tasks SET status = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE booking_tasks SET currentRetryCount = :retryCount, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateRetryCount(id: Long, retryCount: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE booking_tasks SET isActive = 1, status = 'PENDING', currentRetryCount = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun resetTask(id: Long, timestamp: Long = System.currentTimeMillis())
}
