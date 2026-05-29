package com.ticket12306.android

import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.dao.StationDao
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.model.Station
import com.ticket12306.android.data.remote.api.StationApi
import com.ticket12306.android.data.repository.StationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
 * StationRepository 单元测试
 * 测试范围：
 * 1. 车站数据解析（12306数据格式）
 * 2. 网络获取和缓存（成功/失败场景）
 * 3. 缓存过期检查
 * 4. 热门车站逻辑
 * 5. 本地查询（按代码、名称搜索）
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StationRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var stationApi: StationApi

    @Mock
    private lateinit var database: AppDatabase

    @Mock
    private lateinit var stationDao: StationDao

    @Mock
    private lateinit var userPreferences: UserPreferences

    private lateinit var stationRepository: StationRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(database.stationDao()).thenReturn(stationDao)
        stationRepository = StationRepository(stationApi, database, userPreferences)
    }

    // ==================== 车站数据解析测试 ====================

    /** 测试解析标准12306车站数据格式 */
    @Test
    fun fetchStations_validData_parsesAndCachesStations() = testScope.runTest {
        val stationData = "var station_names ='@bjb|北京北|VAP|beijingbei|bjb|0|0@bjn|北京南|VNP|beijingnan|bjn|1|0';"
        val responseBody = ResponseBody.create(null, stationData)
        val response = Response.success(stationData)

        `when`(stationApi.getStationNames()).thenReturn(response)
        `when`(stationDao.replaceAllStations(anyList())).thenReturn(Unit)
        `when`(userPreferences.stationCacheTimestamp).thenReturn(MutableStateFlow(0L))
        `when`(userPreferences.saveStationCacheTimestamp(anyLong())).thenReturn(Unit)

        val result = stationRepository.fetchStations()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        val stations = result.getOrDefault(emptyList())
        assertEquals(2, stations.size)
        assertEquals("VAP", stations[0].code)
        assertEquals("北京北", stations[0].name)
        assertEquals("beijingbei", stations[0].pinyin)
        assertEquals("VNP", stations[1].code)
        assertEquals("北京南", stations[1].name)
    }

    /** 测试解析空数据，返回空列表 */
    @Test
    fun fetchStations_emptyData_returnsEmptyList() = testScope.runTest {
        val stationData = "var station_names ='';"
        val response = Response.success(stationData)

        `when`(stationApi.getStationNames()).thenReturn(response)
        `when`(stationDao.replaceAllStations(anyList())).thenReturn(Unit)
        `when`(userPreferences.stationCacheTimestamp).thenReturn(MutableStateFlow(0L))
        `when`(userPreferences.saveStationCacheTimestamp(anyLong())).thenReturn(Unit)

        val result = stationRepository.fetchStations()
        advanceUntilIdle()

        assertTrue(result.isSuccess)
        val stations = result.getOrDefault(emptyList())
        assertTrue(stations.isEmpty())
    }

    /** 测试网络请求失败，返回失败结果 */
    @Test
    fun fetchStations_networkFailure_returnsFailure() = testScope.runTest {
        `when`(stationApi.getStationNames()).thenThrow(RuntimeException("网络错误"))

        val result = stationRepository.fetchStations()
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    /** 测试服务器返回非成功响应码 */
    @Test
    fun fetchStations_unsuccessfulResponse_returnsFailure() = testScope.runTest {
        val response = Response.error<String>(500, ResponseBody.create(null, "Server Error"))
        `when`(stationApi.getStationNames()).thenReturn(response)

        val result = stationRepository.fetchStations()
        advanceUntilIdle()

        assertTrue(result.isFailure)
    }

    // ==================== 缓存过期检查测试 ====================

    /** 测试从未缓存过数据，返回已过期 */
    @Test
    fun isCacheExpired_neverCached_returnsTrue() = testScope.runTest {
        `when`(userPreferences.stationCacheTimestamp).thenReturn(MutableStateFlow(0L))

        val expired = stationRepository.isCacheExpired()

        assertTrue(expired)
    }

    /** 测试缓存时间在有效期内，返回未过期 */
    @Test
    fun isCacheExpired_withinExpiry_returnsFalse() = testScope.runTest {
        val recentTimestamp = System.currentTimeMillis() - 1000L * 60 * 60
        `when`(userPreferences.stationCacheTimestamp).thenReturn(MutableStateFlow(recentTimestamp))

        val expired = stationRepository.isCacheExpired()

        assertFalse(expired)
    }

    /** 测试缓存时间超过7天，返回已过期 */
    @Test
    fun isCacheExpired_beyondExpiry_returnsTrue() = testScope.runTest {
        val oldTimestamp = System.currentTimeMillis() - 8 * 24 * 60 * 60 * 1000L
        `when`(userPreferences.stationCacheTimestamp).thenReturn(MutableStateFlow(oldTimestamp))

        val expired = stationRepository.isCacheExpired()

        assertTrue(expired)
    }

    // ==================== 本地查询测试 ====================

    /** 测试按代码精确查询车站 */
    @Test
    fun getStationByCode_returnsCorrectStation() = testScope.runTest {
        val station = Station("VNP", "北京南", "beijingnan", "bjn", "1", "0")
        `when`(stationDao.getStationByCode("VNP")).thenReturn(station)

        val result = stationRepository.getStationByCode("VNP")

        assertNotNull(result)
        assertEquals("北京南", result?.name)
    }

    /** 测试按名称精确查询车站 */
    @Test
    fun getStationByName_returnsCorrectStation() = testScope.runTest {
        val station = Station("VNP", "北京南", "beijingnan", "bjn", "1", "0")
        `when`(stationDao.getStationByName("北京南")).thenReturn(station)

        val result = stationRepository.getStationByName("北京南")

        assertNotNull(result)
        assertEquals("VNP", result?.code)
    }

    /** 测试查询不存在的车站，返回null */
    @Test
    fun getStationByCode_nonExistent_returnsNull() = testScope.runTest {
        `when`(stationDao.getStationByCode("XXX")).thenReturn(null)

        val result = stationRepository.getStationByCode("XXX")

        assertNull(result)
    }

    // ==================== 热门车站测试 ====================

    /** 测试热门车站代码列表不为空 */
    @Test
    fun hotStationCodes_isNotEmpty() {
        assertTrue(StationRepository.HOT_STATION_CODES.isNotEmpty())
    }

    /** 测试热门车站名称映射包含北京和上海 */
    @Test
    fun hotStationNames_containsMajorCities() {
        assertTrue(StationRepository.HOT_STATION_NAMES.containsKey("BJP"))
        assertTrue(StationRepository.HOT_STATION_NAMES.containsKey("SHH"))
        assertEquals("北京", StationRepository.HOT_STATION_NAMES["BJP"])
        assertEquals("上海", StationRepository.HOT_STATION_NAMES["SHH"])
    }

    // ==================== 缓存数据检查测试 ====================

    /** 测试检查是否有车站数据 */
    @Test
    fun hasStations_withData_returnsTrue() = testScope.runTest {
        `when`(stationDao.getStationCount()).thenReturn(2800)

        val hasStations = stationRepository.hasStations()

        assertTrue(hasStations)
    }

    /** 测试检查无车站数据 */
    @Test
    fun hasStations_noData_returnsFalse() = testScope.runTest {
        `when`(stationDao.getStationCount()).thenReturn(0)

        val hasStations = stationRepository.hasStations()

        assertFalse(hasStations)
    }

    /** 测试获取车站数量 */
    @Test
    fun getStationCount_returnsCorrectCount() = testScope.runTest {
        `when`(stationDao.getStationCount()).thenReturn(2800)

        val count = stationRepository.getStationCount()

        assertEquals(2800, count)
    }
}
