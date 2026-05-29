package com.ticket12306.android.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "stations")
@Parcelize
data class Station(
    @PrimaryKey
    val code: String,
    val name: String,
    val pinyin: String,
    val pinyinInitial: String,
    val province: String? = null,
    val city: String? = null
) : Parcelable {
    fun matchesQuery(query: String): Boolean {
        val lowerQuery = query.lowercase()
        return name.contains(query) ||
                pinyin.lowercase().contains(lowerQuery) ||
                pinyinInitial.lowercase().contains(lowerQuery) ||
                code.lowercase().contains(lowerQuery)
    }
}
