package com.ticket12306.android.data.repository

import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.model.ApiResponse
import com.ticket12306.android.data.model.CaptchaCheckRequest
import com.ticket12306.android.data.model.CaptchaCheckResponse
import com.ticket12306.android.data.model.CaptchaResponse
import com.ticket12306.android.data.model.LoginRequest
import com.ticket12306.android.data.model.LoginResponse
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.data.remote.RetrofitClient
import com.ticket12306.android.data.remote.api.OrderApi
import com.ticket12306.android.data.remote.api.UserApi
import kotlinx.coroutines.flow.first

class UserRepository(
    private val userApi: UserApi,
    private val orderApi: OrderApi,
    private val database: AppDatabase,
    private val userPreferences: UserPreferences
) {

    val isLoggedIn = userPreferences.isLoggedIn
    val userName = userPreferences.userName
    val username = userPreferences.username

    suspend fun login(username: String, password: String, captchaAnswer: String): Result<LoginResponse> {
        return try {
            val request = LoginRequest(
                username = username,
                password = password,
                answer = captchaAnswer
            )
            val response = userApi.login(request)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.isSuccess == true && body.data != null) {
                    userPreferences.saveLoginInfo(
                        token = body.data.uamtk,
                        username = username,
                        userName = body.data.name
                    )
                    RetrofitClient.updateToken(body.data.uamtk)
                    Result.success(body.data)
                } else {
                    Result.failure(Exception(body?.result_message ?: "登录失败"))
                }
            } else {
                Result.failure(Exception("网络请求失败: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            val response = userApi.logout()
            userPreferences.clearLoginInfo()
            RetrofitClient.updateToken(null)
            RetrofitClient.clearCookies()
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            userPreferences.clearLoginInfo()
            Result.success(Unit)
        }
    }

    suspend fun getCaptcha(): Result<CaptchaResponse> {
        return try {
            val response = userApi.getCaptcha()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("获取验证码失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkCaptcha(answer: String): Result<CaptchaCheckResponse> {
        return try {
            val request = CaptchaCheckRequest(answer = answer)
            val response = userApi.checkCaptcha(request)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception("验证码验证失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchPassengers(): Result<List<Passenger>> {
        return try {
            val response = userApi.getPassengers()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                val passengers = response.body()!!.data?.result ?: emptyList()
                database.passengerDao().insertPassengers(passengers)
                Result.success(passengers)
            } else {
                Result.failure(Exception(response.body()?.result_message ?: "获取乘客信息失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isLoggedInSync(): Boolean {
        return userPreferences.isLoggedIn.first()
    }

    suspend fun getToken(): String? {
        return userPreferences.token.first()
    }
}
