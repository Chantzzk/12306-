package com.ticket12306.android

import com.ticket12306.android.data.local.dao.PassengerDao
import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.model.*
import com.ticket12306.android.data.remote.api.OrderApi
import com.ticket12306.android.data.remote.api.UserApi
import com.ticket12306.android.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import retrofit2.Response

/**
 * UserRepository 单元测试
 * 测试范围：
 * 1. 登录流程（成功/失败场景）
 * 2. 退出登录（网络成功/失败都清除本地信息）
 * 3. 验证码获取和校验
 * 4. 乘客信息获取
 * 5. 登录状态检查
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var userApi: UserApi

    @Mock
    private lateinit var orderApi: OrderApi

    @Mock
    private lateinit var database: AppDatabase

    @Mock
    private lateinit var userPreferences: UserPreferences

    @Mock
    private lateinit var passengerDao: PassengerDao

    private lateinit var userRepository: UserRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(database.passengerDao()).thenReturn(passengerDao)
        `when`(userPreferences.isLoggedIn).thenReturn(MutableStateFlow(false))
        `when`(userPreferences.userName).thenReturn(MutableStateFlow(null))
        `when`(userPreferences.username).thenReturn(MutableStateFlow(null))
        `when`(userPreferences.token).thenReturn(MutableStateFlow(null))
        userRepository = UserRepository(userApi, orderApi, database, userPreferences)
    }

    // ==================== 登录流程测试 ====================

    /** 测试登录成功，验证返回LoginResponse且保存登录信息 */
    @Test
    fun login_success_returnsLoginResponseAndSavesInfo() = testScope.runTest {
        val loginResponse = LoginResponse(
            uamtk = "token_123",
            newapptk = "new_token_456",
            username = "testuser",
            name = "测试用户"
        )
        val apiResponse = ApiResponse(data = loginResponse, result_code = 0)
        val response = Response.success(apiResponse)

        `when`(userApi.login(any())).thenReturn(response)

        val result = userRepository.login("testuser", "password123", "1234")
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        val response_data = result.getOrNull()
        assertNotNull(response_data)
        assertEquals("token_123", response_data!!.uamtk)
        assertEquals("测试用户", response_data.name)
        verify(userPreferences).saveLoginInfo("token_123", "testuser", "测试用户")
    }

    /** 测试登录失败（API返回错误），验证返回失败结果 */
    @Test
    fun login_apiError_returnsFailure() = testScope.runTest {
        val apiResponse = ApiResponse<LoginResponse>(
            result_code = 1,
            result_message = "用户名或密码错误",
            data = null
        )
        val response = Response.success(apiResponse)

        `when`(userApi.login(any())).thenReturn(response)

        val result = userRepository.login("testuser", "wrongpass", "1234")
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    /** 测试登录网络异常，验证返回失败结果 */
    @Test
    fun login_networkException_returnsFailure() = testScope.runTest {
        `when`(userApi.login(any())).thenThrow(RuntimeException("网络连接失败"))

        val result = userRepository.login("testuser", "password123", "1234")
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    /** 测试登录HTTP错误响应码，验证返回失败结果 */
    @Test
    fun login_httpError_returnsFailure() = testScope.runTest {
        val response = Response.error<ApiResponse<LoginResponse>>(
            401, ResponseBody.create(null, "Unauthorized")
        )

        `when`(userApi.login(any())).thenReturn(response)

        val result = userRepository.login("testuser", "password123", "1234")
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    // ==================== 退出登录测试 ====================

    /** 测试退出登录成功，验证清除本地登录信息 */
    @Test
    fun logout_success_clearsLoginInfo() = testScope.runTest {
        val apiResponse = ApiResponse<Unit>(result_code = 0)
        val response = Response.success(apiResponse)

        `when`(userApi.logout()).thenReturn(response)

        val result = userRepository.logout()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        verify(userPreferences).clearLoginInfo()
    }

    /** 测试退出登录网络异常，仍然清除本地登录信息 */
    @Test
    fun logout_networkException_stillClearsLoginInfo() = testScope.runTest {
        `when`(userApi.logout()).thenThrow(RuntimeException("网络错误"))

        val result = userRepository.logout()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        verify(userPreferences).clearLoginInfo()
    }

    // ==================== 验证码测试 ====================

    /** 测试获取验证码成功 */
    @Test
    fun getCaptcha_success_returnsCaptchaResponse() = testScope.runTest {
        val captchaResponse = CaptchaResponse(image = "base64imagedata", result = 0)
        val apiResponse = ApiResponse(data = captchaResponse, result_code = 0)
        val response = Response.success(apiResponse)

        `when`(userApi.getCaptcha()).thenReturn(response)

        val result = userRepository.getCaptcha()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals("base64imagedata", result.getOrNull()?.image)
    }

    /** 测试获取验证码失败 */
    @Test
    fun getCaptcha_failure_returnsFailure() = testScope.runTest {
        `when`(userApi.getCaptcha()).thenThrow(RuntimeException("网络错误"))

        val result = userRepository.getCaptcha()
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    /** 测试验证码校验成功 */
    @Test
    fun checkCaptcha_success_returnsCheckResponse() = testScope.runTest {
        val checkResponse = CaptchaCheckResponse(result_code = 4, result_message = "验证码校验成功")
        val apiResponse = ApiResponse(data = checkResponse, result_code = 0)
        val response = Response.success(apiResponse)

        `when`(userApi.checkCaptcha(any())).thenReturn(response)

        val result = userRepository.checkCaptcha("1234")
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(4, result.getOrNull()?.result_code)
    }

    /** 测试验证码校验失败 */
    @Test
    fun checkCaptcha_failure_returnsFailure() = testScope.runTest {
        val apiResponse = ApiResponse<CaptchaCheckResponse>(
            result_code = 1,
            result_message = "验证码校验失败",
            data = null
        )
        val response = Response.success(apiResponse)

        `when`(userApi.checkCaptcha(any())).thenReturn(response)

        val result = userRepository.checkCaptcha("wrong")
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    // ==================== 乘客信息测试 ====================

    /** 测试获取乘客信息成功，验证数据缓存到数据库 */
    @Test
    fun fetchPassengers_success_returnsAndCachesPassengers() = testScope.runTest {
        val passengers = listOf(
            Passenger(
                code = "P001", passenger_name = "张三", sex_code = "M",
                sex_name = "男", born_date = "1990-01-01", country_code = "CN",
                passenger_id_type_code = "1", passenger_id_type_name = "二代身份证",
                passenger_id_no = "110101199001011234", passenger_type = "1",
                passenger_flag = "0", passenger_name_en = "ZHANG SAN"
            )
        )
        val passengerResponse = PassengerResponse(result = passengers, map = emptyMap())
        val apiResponse = ApiResponse(data = passengerResponse, result_code = 0)
        val response = Response.success(apiResponse)

        `when`(userApi.getPassengers()).thenReturn(response)
        `when`(passengerDao.insertPassengers(anyList())).thenReturn(Unit)

        val result = userRepository.fetchPassengers()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("张三", result.getOrNull()?.get(0)?.passenger_name)
        verify(passengerDao).insertPassengers(anyList())
    }

    /** 测试获取乘客信息失败，返回失败结果 */
    @Test
    fun fetchPassengers_failure_returnsFailure() = testScope.runTest {
        `when`(userApi.getPassengers()).thenThrow(RuntimeException("网络错误"))

        val result = userRepository.fetchPassengers()
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    // ==================== 登录状态检查测试 ====================

    /** 测试同步检查登录状态 - 已登录 */
    @Test
    fun isLoggedInSync_loggedIn_returnsTrue() = testScope.runTest {
        `when`(userPreferences.isLoggedIn).thenReturn(MutableStateFlow(true))

        val loggedIn = userRepository.isLoggedInSync()

        assertTrue(loggedIn)
    }

    /** 测试同步检查登录状态 - 未登录 */
    @Test
    fun isLoggedInSync_notLoggedIn_returnsFalse() = testScope.runTest {
        `when`(userPreferences.isLoggedIn).thenReturn(MutableStateFlow(false))

        val loggedIn = userRepository.isLoggedInSync()

        assertFalse(loggedIn)
    }

    /** 测试获取Token */
    @Test
    fun getToken_withToken_returnsToken() = testScope.runTest {
        `when`(userPreferences.token).thenReturn(MutableStateFlow("my_token"))

        val token = userRepository.getToken()

        assertEquals("my_token", token)
    }

    /** 测试获取Token - 无Token */
    @Test
    fun getToken_noToken_returnsNull() = testScope.runTest {
        `when`(userPreferences.token).thenReturn(MutableStateFlow(null))

        val token = userRepository.getToken()

        assertNull(token)
    }
}
