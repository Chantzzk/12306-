package com.ticket12306.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StationResponse(
    val stations: List<StationData>,
    val version: String? = null
) : Parcelable

@Parcelize
data class StationData(
    val code: String,
    val name: String,
    val pinyin: String,
    val pinyinInitial: String,
    val province: String? = null,
    val city: String? = null
) : Parcelable

data class StationParseResult(
    val success: Boolean,
    val stations: List<StationData> = emptyList(),
    val error: String? = null
)
