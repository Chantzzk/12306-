package com.ticket12306.android.data.remote

import com.ticket12306.android.BuildConfig
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://kyfw.12306.cn/"
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private lateinit var userPreferences: UserPreferences
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    @Volatile
    private var cachedToken: String? = null

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            cookieStore[host] = (cookieStore[host] ?: mutableListOf()).apply {
                removeAll(cookies)
                addAll(cookies)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: emptyList()
        }
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
    }

    @PublishedApi
    internal val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addInterceptor(headerInterceptor())
            .addInterceptor(authInterceptor())
            .addInterceptor(loggingInterceptor())
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun headerInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Connection", "keep-alive")
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .build()
            chain.proceed(request)
        }
    }

    private fun authInterceptor(): Interceptor {
        return Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            cachedToken?.let { token ->
                if (token.isNotEmpty()) {
                    requestBuilder.addHeader("Cookie", "JSESSIONID=$token")
                }
            }
            chain.proceed(requestBuilder.build())
        }
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    fun init(userPrefs: UserPreferences) {
        userPreferences = userPrefs
    }

    fun updateToken(token: String?) {
        cachedToken = token
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> create(): T {
        return retrofit.create(T::class.java)
    }

    fun clearCookies() {
        cookieStore.clear()
    }

    fun getCookies(): Map<String, List<Cookie>> {
        return cookieStore.toMap()
    }

    fun addCookie(url: HttpUrl, cookie: Cookie) {
        val host = url.host
        cookieStore[host] = (cookieStore[host] ?: mutableListOf()).apply {
            add(cookie)
        }
    }
}
