package com.ticket12306.android.data.remote.api

import com.ticket12306.android.data.model.ApiResponse
import com.ticket12306.android.data.model.CancelOrderRequest
import com.ticket12306.android.data.model.CancelOrderResponse
import com.ticket12306.android.data.model.CheckOrderRequest
import com.ticket12306.android.data.model.CheckOrderResponse
import com.ticket12306.android.data.model.ConfirmOrderRequest
import com.ticket12306.android.data.model.OrderInitResponse
import com.ticket12306.android.data.model.OrderListResponse
import com.ticket12306.android.data.model.OrderQueueInfoResponse
import com.ticket12306.android.data.model.OrderRequest
import com.ticket12306.android.data.model.OrderResult
import com.ticket12306.android.data.model.OrderResultResponse
import com.ticket12306.android.data.model.OrderSubmitResponse
import com.ticket12306.android.data.model.OrderWaitTimeResponse
import com.ticket12306.android.data.model.PassengerDTOsResponse
import com.ticket12306.android.data.model.PaymentInfoResponse
import com.ticket12306.android.data.model.PayCheckRequest
import com.ticket12306.android.data.model.PayCheckResponse
import com.ticket12306.android.data.model.QueueCountRequest
import com.ticket12306.android.data.model.QueueCountResponse
import com.ticket12306.android.data.model.ResultOrderRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApi {

    @POST("otn/leftTicket/submitOrderRequest")
    suspend fun submitOrderRequest(
        @Body request: OrderRequest
    ): Response<ApiResponse<String>>

    @POST("otn/leftTicket/initDc")
    suspend fun initOrder(): Response<OrderInitResponse>

    @GET("otn/confirmPassenger/getPassengerDTOs")
    suspend fun getPassengerDTOs(): Response<ApiResponse<PassengerDTOsResponse>>

    @POST("otn/confirmPassenger/checkOrderInfo")
    suspend fun checkOrderInfo(
        @Body request: CheckOrderRequest
    ): Response<ApiResponse<CheckOrderResponse>>

    @POST("otn/confirmPassenger/getQueueCount")
    suspend fun getQueueCount(
        @Body request: QueueCountRequest
    ): Response<ApiResponse<QueueCountResponse>>

    @POST("otn/confirmPassenger/confirmSingleForQueue")
    suspend fun confirmSingleForQueue(
        @Body request: ConfirmOrderRequest
    ): Response<ApiResponse<OrderResult>>

    @POST("otn/confirmPassenger/confirmGoForQueue")
    suspend fun confirmGoForQueue(
        @Body request: ConfirmOrderRequest
    ): Response<ApiResponse<OrderResult>>

    @GET("otn/confirmPassenger/queryOrderWaitTime")
    suspend fun queryOrderWaitTime(
        @Query("tourFlag") tourFlag: String = "dc",
        @Query("random") random: String
    ): Response<ApiResponse<OrderWaitTimeResponse>>

    @POST("otn/confirmPassenger/resultOrderForDcQueue")
    suspend fun resultOrderForDcQueue(
        @Body request: ResultOrderRequest
    ): Response<ApiResponse<OrderResultResponse>>

    @GET("otn/queryOrder/queryMyOrder")
    suspend fun queryMyOrder(
        @Query("queryType") queryType: String = "1",
        @Query("queryStartDate") startDate: String,
        @Query("queryEndDate") endDate: String,
        @Query("come_from_flag") comeFromFlag: String = "my_order"
    ): Response<ApiResponse<OrderListResponse>>

    @GET("otn/queryOrder/queryMyOrderNoComplete")
    suspend fun queryUnpaidOrder(): Response<ApiResponse<OrderListResponse>>

    @POST("otn/pay/payCheck")
    suspend fun payCheck(
        @Body request: PayCheckRequest
    ): Response<ApiResponse<PayCheckResponse>>

    @GET("otn/pay/payOrder")
    suspend fun getPaymentInfo(
        @Query("sequence_no") sequenceNo: String
    ): Response<PaymentInfoResponse>

    @POST("otn/order/cancelOrder")
    suspend fun cancelOrder(
        @Body request: CancelOrderRequest
    ): Response<ApiResponse<CancelOrderResponse>>

    companion object {
        const val TOUR_FLAG_DC = "dc"
        const val TOUR_FLAG_WC = "wc"
        const val TOUR_FLAG_FC = "fc"
        const val TOUR_FLAG_GC = "gc"
    }
}
