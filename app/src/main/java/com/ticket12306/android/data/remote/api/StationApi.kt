package com.ticket12306.android.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StationApi {

    @GET("otn/resources/js/framework/station_name.js")
    suspend fun getStationNames(): Response<String>

    @GET("otn/resources/js/framework/station_name.js")
    suspend fun getStationNamesWithQuery(
        @Query("station_version") version: String
    ): Response<String>

    companion object {
        const val STATION_DATA_URL = "otn/resources/js/framework/station_name.js"
    }
}
