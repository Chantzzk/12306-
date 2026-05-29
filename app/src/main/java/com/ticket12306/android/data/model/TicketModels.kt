package com.ticket12306.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TicketQueryRequest(
    val trainDate: String,
    val fromStation: String,
    val toStation: String,
    val purposeCodes: String = "ADULT"
) : Parcelable

@Parcelize
data class TicketQueryResponse(
    val result: List<TicketResultItem>,
    val map: Map<String, String> = emptyMap()
) : Parcelable

@Parcelize
data class TicketResultItem(
    val secretStr: String,
    val buttonTextInfo: String,
    val trainNo: String,
    val trainCode: String,
    val startStationTelecode: String,
    val endStationTelecode: String,
    val fromStationTelecode: String,
    val toStationTelecode: String,
    val startTime: String,
    val arriveTime: String,
    val dayDifference: String,
    val trainClassName: String,
    val duration: String,
    val canWebBuy: String,
    val ypInfo: String,
    val seatPrices: Map<String, SeatPriceInfo> = emptyMap()
) : Parcelable

@Parcelize
data class SeatPriceInfo(
    val seatType: String,
    val seatTypeName: String,
    val price: Double,
    val remainCount: Int,
    val canBuy: Boolean
) : Parcelable

@Parcelize
data class TrainDetailInfo(
    val trainCode: String,
    val trainNo: String,
    val startStation: String,
    val endStation: String,
    val fromStation: String,
    val toStation: String,
    val startTime: String,
    val arriveTime: String,
    val duration: String,
    val dayDifference: Int = 0,
    val trainClassName: String,
    val seatList: List<TrainSeatInfo>
) : Parcelable

@Parcelize
data class TrainSeatInfo(
    val seatType: String,
    val seatTypeName: String,
    val price: Double,
    val remainCount: Int,
    val canBuy: Boolean
) : Parcelable
