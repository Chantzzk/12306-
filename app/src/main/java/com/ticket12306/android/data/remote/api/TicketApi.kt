package com.ticket12306.android.data.remote.api

import com.ticket12306.android.data.model.ApiResponse
import com.ticket12306.android.data.model.TicketQueryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TicketApi {

    @GET("otn/leftTicket/queryA")
    suspend fun queryTickets(
        @Query("leftTicketDTO.train_date") trainDate: String,
        @Query("leftTicketDTO.from_station") fromStation: String,
        @Query("leftTicketDTO.to_station") toStation: String,
        @Query("purpose_codes") purposeCodes: String = "ADULT"
    ): Response<ApiResponse<TicketQueryResponse>>

    @GET("otn/leftTicket/query")
    suspend fun queryTicketsV2(
        @Query("leftTicketDTO.train_date") trainDate: String,
        @Query("leftTicketDTO.from_station") fromStation: String,
        @Query("leftTicketDTO.to_station") toStation: String,
        @Query("purpose_codes") purposeCodes: String = "ADULT"
    ): Response<ApiResponse<TicketQueryResponse>>

    @GET("otn/leftTicket/queryZ")
    suspend fun queryTicketsStudent(
        @Query("leftTicketDTO.train_date") trainDate: String,
        @Query("leftTicketDTO.from_station") fromStation: String,
        @Query("leftTicketDTO.to_station") toStation: String,
        @Query("purpose_codes") purposeCodes: String = "0X00"
    ): Response<ApiResponse<TicketQueryResponse>>

    companion object {
        const val PURPOSE_ADULT = "ADULT"
        const val PURPOSE_STUDENT = "0X00"
    }
}
