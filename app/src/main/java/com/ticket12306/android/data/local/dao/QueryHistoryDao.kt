package com.ticket12306.android.data.local.dao

import androidx.room.*
import com.ticket12306.android.data.local.entity.QueryHistoryEntity
import com.ticket12306.android.data.local.entity.QueryType
import kotlinx.coroutines.flow.Flow

@Dao
interface QueryHistoryDao {

    @Query("SELECT * FROM query_history WHERE userId = :userId ORDER BY queryTime DESC")
    fun getQueryHistoryByUser(userId: Long): Flow<List<QueryHistoryEntity>>

    @Query("SELECT * FROM query_history WHERE userId = :userId AND queryType = :queryType ORDER BY queryTime DESC")
    fun getQueryHistoryByUserAndType(userId: Long, queryType: QueryType): Flow<List<QueryHistoryEntity>>

    @Query("SELECT * FROM query_history WHERE userId = :userId ORDER BY queryTime DESC LIMIT :limit")
    fun getRecentQueryHistory(userId: Long, limit: Int = 10): Flow<List<QueryHistoryEntity>>

    @Query("SELECT * FROM query_history WHERE id = :id")
    suspend fun getQueryHistoryById(id: Long): QueryHistoryEntity?

    @Query("SELECT * FROM query_history WHERE userId = :userId AND fromStationCode = :fromCode AND toStationCode = :toCode AND trainDate = :trainDate LIMIT 1")
    suspend fun findDuplicateQuery(userId: Long, fromCode: String, toCode: String, trainDate: String): QueryHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueryHistory(history: QueryHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueryHistoryList(histories: List<QueryHistoryEntity>)

    @Update
    suspend fun updateQueryHistory(history: QueryHistoryEntity)

    @Delete
    suspend fun deleteQueryHistory(history: QueryHistoryEntity)

    @Query("DELETE FROM query_history WHERE id = :id")
    suspend fun deleteQueryHistoryById(id: Long)

    @Query("DELETE FROM query_history WHERE userId = :userId")
    suspend fun deleteQueryHistoryByUser(userId: Long)

    @Query("DELETE FROM query_history WHERE userId = :userId AND queryType = :queryType")
    suspend fun deleteQueryHistoryByUserAndType(userId: Long, queryType: QueryType)

    @Query("DELETE FROM query_history")
    suspend fun deleteAllQueryHistory()

    @Query("DELETE FROM query_history WHERE queryTime < :timestamp")
    suspend fun deleteOldQueryHistory(timestamp: Long)

    @Query("SELECT COUNT(*) FROM query_history WHERE userId = :userId")
    suspend fun getQueryHistoryCount(userId: Long): Int

    @Query("SELECT DISTINCT fromStationCode, fromStationName, toStationCode, toStationName FROM query_history WHERE userId = :userId ORDER BY queryTime DESC LIMIT :limit")
    fun getDistinctRoutes(userId: Long, limit: Int = 5): Flow<List<RouteInfo>>

    @Transaction
    suspend fun insertOrUpdateQueryHistory(history: QueryHistoryEntity): Long {
        val existing = findDuplicateQuery(
            history.userId,
            history.fromStationCode,
            history.toStationCode,
            history.trainDate
        )
        return if (existing != null) {
            val updated = history.copy(id = existing.id, queryTime = System.currentTimeMillis())
            updateQueryHistory(updated)
            existing.id
        } else {
            insertQueryHistory(history)
        }
    }
}

data class RouteInfo(
    val fromStationCode: String,
    val fromStationName: String,
    val toStationCode: String,
    val toStationName: String
)
