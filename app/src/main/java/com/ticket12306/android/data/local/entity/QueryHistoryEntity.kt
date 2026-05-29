package com.ticket12306.android.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = "query_history",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"]), Index(value = ["queryTime"])]
)
@Parcelize
data class QueryHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long = 0,
    val fromStationCode: String,
    val fromStationName: String,
    val toStationCode: String,
    val toStationName: String,
    val trainDate: String,
    val queryTime: Long = System.currentTimeMillis(),
    val queryType: QueryType = QueryType.TICKET
) : Parcelable

enum class QueryType {
    TICKET,
    ORDER,
    STATION
}
