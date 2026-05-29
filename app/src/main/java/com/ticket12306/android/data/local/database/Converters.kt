package com.ticket12306.android.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ticket12306.android.data.local.entity.QueryType

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromMap(value: Map<String, String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toMap(value: String): Map<String, String> {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return gson.fromJson(value, mapType)
    }

    @TypeConverter
    fun fromQueryType(queryType: QueryType): String {
        return queryType.name
    }

    @TypeConverter
    fun toQueryType(value: String): QueryType {
        return try {
            QueryType.valueOf(value)
        } catch (e: IllegalArgumentException) {
            QueryType.TICKET
        }
    }
}
