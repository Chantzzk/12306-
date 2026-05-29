package com.ticket12306.android.data.remote.api

import com.ticket12306.android.data.model.ApiResponse
import com.ticket12306.android.data.model.CaptchaCheckRequest
import com.ticket12306.android.data.model.CaptchaCheckResponse
import com.ticket12306.android.data.model.CaptchaResponse
import com.ticket12306.android.data.model.EncryptRequest
import com.ticket12306.android.data.model.EncryptResponse
import com.ticket12306.android.data.model.LoginRequest
import com.ticket12306.android.data.model.LoginResponse
import com.ticket12306.android.data.model.PassengerResponse
import com.ticket12306.android.data.model.UserCheckResponse
import com.ticket12306.android.data.model.UserInfoResponse
import com.ticket12306.android.data.model.UserUamtkResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {

    @POST("passport/web/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @POST("passport/web/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @GET("passport/web/auth/uamtk")
    suspend fun getUamtk(): Response<ApiResponse<UserUamtkResponse>>

    @POST("passport/web/auth/uamtkClient")
    suspend fun uamtkClient(
        @Query("tk") tk: String
    ): Response<ApiResponse<UserCheckResponse>>

    @POST("passport/captcha/captcha-image")
    suspend fun getCaptcha(): Response<ApiResponse<CaptchaResponse>>

    @POST("passport/captcha/captcha-check")
    suspend fun checkCaptcha(
        @Body request: CaptchaCheckRequest
    ): Response<ApiResponse<CaptchaCheckResponse>>

    @GET("otn/confirmPassenger/getPassengerDTOs")
    suspend fun getPassengers(): Response<ApiResponse<PassengerResponse>>

    @GET("otn/modifyUser/query")
    suspend fun getUserInfo(): Response<ApiResponse<UserInfoResponse>>

    @POST("passport/web/encrypt")
    suspend fun encryptPassword(
        @Body request: EncryptRequest
    ): Response<ApiResponse<EncryptResponse>>

    companion object {
        const val LOGIN_SITE = "E"
        const val RAND_TYPE = "sjrand"
    }
}
