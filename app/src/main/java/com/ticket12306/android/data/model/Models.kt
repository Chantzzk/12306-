package com.ticket12306.android.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginRequest(
    val username: String,
    val password: String,
    val answer: String,
    val login_site: String = "E"
) : Parcelable

@Parcelize
data class LoginResponse(
    val uamtk: String,
    val newapptk: String,
    val username: String,
    val name: String
) : Parcelable

@Parcelize
data class TicketInfo(
    val trainCode: String,
    val trainNo: String,
    val startStation: String,
    val endStation: String,
    val fromStation: String,
    val toStation: String,
    val startTime: String,
    val arriveTime: String,
    val dayDifference: String,
    val trainClassName: String,
    val duration: String,
    val canWebBuy: String,
    val seatTypes: Map<String, SeatInfo>
) : Parcelable

@Parcelize
data class SeatInfo(
    val seatType: String,
    val seatTypeName: String,
    val price: Double,
    val remainTicket: Int,
    val canBuy: Boolean
) : Parcelable

@Parcelize
data class OrderRequest(
    val secretStr: String,
    val train_date: String,
    val back_train_date: String,
    val tour_flag: String = "dc",
    val purpose_codes: String = "ADULT",
    val query_from_station_name: String,
    val query_to_station_name: String
) : Parcelable

@Parcelize
data class ConfirmOrderRequest(
    val passengerTicketStr: String,
    val oldPassengerStr: String,
    val randCode: String,
    val key_check_isChange: String,
    val leftTicketStr: String,
    val train_location: String
) : Parcelable

@Parcelize
data class OrderResult(
    val submitStatus: Boolean,
    val orderSequence: String?,
    val errorMessage: String?
) : Parcelable

@Parcelize
data class PassengerResponse(
    val result: List<Passenger>,
    val map: Map<String, String>
) : Parcelable

@Entity(tableName = "passengers")
@Parcelize
data class Passenger(
    @PrimaryKey
    val code: String,
    val passenger_name: String,
    val sex_code: String,
    val sex_name: String,
    val born_date: String,
    val country_code: String,
    val passenger_id_type_code: String,
    val passenger_id_type_name: String,
    val passenger_id_no: String,
    val passenger_type: String,
    val passenger_flag: String,
    val passenger_name_en: String
) : Parcelable

@Parcelize
data class OrderListResponse(
    val orderDTODataList: List<OrderInfo>
) : Parcelable

@Parcelize
data class OrderInfo(
    val sequence_no: String,
    val train_code: String,
    val train_date: String,
    val from_station: String,
    val to_station: String,
    val start_time: String,
    val arrive_time: String,
    val ticket_price: Double,
    val ticket_type_name: String,
    val coach_name: String,
    val seat_name: String,
    val passenger_name: String,
    val order_status: String,
    val pay_status: String
) : Parcelable

@Parcelize
data class CaptchaResponse(
    val image: String,
    val result: Int
) : Parcelable

@Parcelize
data class CaptchaCheckRequest(
    val answer: String,
    val login_site: String = "E",
    val rand: String = "sjrand"
) : Parcelable

@Parcelize
data class CaptchaCheckResponse(
    val result_code: Int,
    val result_message: String
) : Parcelable

data class ApiResponse<T>(
    val result_code: Int = 0,
    val result_message: String = "",
    val data: T? = null
) {
    val isSuccess: Boolean
        get() = result_code == 0
}

/**
 * 座次选择项UI状态模型
 * 封装座次信息和选中状态，用于座次选择列表展示
 */
data class SeatSelectItem(
    val seatInfo: SeatInfo,
    val isSelected: Boolean = false
) {
    /** 余票状态枚举：有票、少量、无票、候补 */
    val ticketStatus: TicketStatus
        get() = when {
            !seatInfo.canBuy -> TicketStatus.WAITLIST
            seatInfo.remainTicket <= 0 -> TicketStatus.NONE
            seatInfo.remainTicket <= 10 -> TicketStatus.FEW
            else -> TicketStatus.ENOUGH
        }

    /** 是否可以被选中（所有座次均可选中，无票/候补选中后为候补模式） */
    val isSelectable: Boolean
        get() = true
}

/** 余票状态枚举 */
enum class TicketStatus {
    ENOUGH,
    FEW,
    NONE,
    WAITLIST
}

/**
 * 座次选择结果
 * 用于传递座次选择和乘客选择的结果到抢票页面
 */
@Parcelize
data class SeatSelectResult(
    val trainInfo: TicketInfo,
    val selectedSeatType: String,
    val selectedSeatTypeName: String,
    val selectedPassengerIds: List<String>,
    val selectedPassengerNames: List<String>,
    val isWaitlist: Boolean = false
) : Parcelable

/**
 * 余票查询结果
 * 封装某车次某座次的余票检查结果
 */
data class TicketCheckResult(
    val hasTicket: Boolean,
    val ticketInfo: TicketInfo? = null,
    val seatInfo: SeatInfo? = null,
    val error: String? = null
)

/**
 * 抢票操作结果
 * 封装一次抢票尝试的最终结果
 */
data class BookingResult(
    val success: Boolean,
    val orderSequence: String? = null,
    val errorMessage: String? = null
)
