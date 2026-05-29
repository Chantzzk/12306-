package com.ticket12306.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderInitResponse(
    val isLogin: Boolean,
    val token: String? = null
) : Parcelable

@Parcelize
data class PassengerDTOsResponse(
    val result: List<Passenger>,
    val map: Map<String, String> = emptyMap()
) : Parcelable

@Parcelize
data class CheckOrderRequest(
    val cancel_flag: String = "2",
    val bed_level_order_num: String = "000000000000000000000000000000",
    val passengerTicketStr: String,
    val oldPassengerStr: String,
    val tour_flag: String = "dc",
    val randCode: String = "",
    val whatsSelect: String = "1",
    val _json_att: String = "",
    val REPEAT_SUBMIT_TOKEN: String
) : Parcelable

@Parcelize
data class CheckOrderResponse(
    val submitStatus: Boolean,
    val isNoActive: Boolean = false,
    val checkSeatNum: String? = null,
    val checkSeatResult: String? = null,
    val errMsg: String? = null
) : Parcelable

@Parcelize
data class QueueCountRequest(
    val train_date: String,
    val train_no: String,
    val stationTrainCode: String,
    val seatType: String,
    val fromStationTelecode: String,
    val toStationTelecode: String,
    val leftTicket: String,
    val purpose_codes: String = "ADULT",
    val train_location: String,
    val _json_att: String = "",
    val REPEAT_SUBMIT_TOKEN: String
) : Parcelable

@Parcelize
data class QueueCountResponse(
    val count: String,
    val ticket: String,
    val op_2: Boolean = false
) : Parcelable

@Parcelize
data class OrderWaitTimeResponse(
    val queryOrderWaitTimeStatus: Boolean,
    val waitCount: Int = 0,
    val waitTime: Int = -1,
    val requestId: String? = null,
    val tourFlag: String? = null
) : Parcelable

@Parcelize
data class ResultOrderRequest(
    val orderSequence_no: String,
    val _json_att: String = "",
    val REPEAT_SUBMIT_TOKEN: String
) : Parcelable

@Parcelize
data class OrderResultResponse(
    val submitStatus: Boolean,
    val orderSequence: String? = null,
    val errMsg: String? = null
) : Parcelable

@Parcelize
data class OrderSubmitResponse(
    val submitStatus: Boolean,
    val token: String? = null,
    val key_check_isChange: String? = null,
    val leftTicketStr: String? = null,
    val train_location: String? = null
) : Parcelable

@Parcelize
data class OrderQueueInfoResponse(
    val queueCount: Int,
    val waitTime: Int,
    val isShowQueue: Boolean = false
) : Parcelable

@Parcelize
data class PayCheckRequest(
    val sequence_no: String,
    val pay_method: String = "Y"
) : Parcelable

@Parcelize
data class PayCheckResponse(
    val payCheckResult: Boolean,
    val payUrl: String? = null
) : Parcelable

@Parcelize
data class CancelOrderRequest(
    val sequence_no: String,
    val cancel_flag: String = "1"
) : Parcelable

@Parcelize
data class CancelOrderResponse(
    val cancelStatus: Boolean,
    val errMsg: String? = null
) : Parcelable

@Parcelize
data class PaymentInfoResponse(
    val payUrl: String? = null,
    val payAmount: Double = 0.0,
    val payStatus: String? = null
) : Parcelable
