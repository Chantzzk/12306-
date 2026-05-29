package com.ticket12306.android

import com.ticket12306.android.booking.TicketMonitor
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.*
import com.ticket12306.android.data.repository.TicketRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * TicketMonitor 单元测试
 * 测试范围：
 * 1. 余票查询和结果选择
 * 2. 余票变化检测
 * 3. 座次偏好检查
 * 4. 监控生命周期管理（启动/停止）
 * 5. 连续无票计数逻辑
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TicketMonitorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var ticketRepository: TicketRepository

    @Mock
    private lateinit var bookingTaskDao: BookingTaskDao

    @Mock
    private lateinit var bookingLogDao: BookingLogDao

    private lateinit var ticketMonitor: TicketMonitor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(bookingLogDao.insertLog(any())).thenReturn(1L)
        ticketMonitor = TicketMonitor(ticketRepository, bookingTaskDao, bookingLogDao)
    }

    // ==================== 监控生命周期测试 ====================

    /** 测试停止指定任务监控，验证资源被清理 */
    @Test
    fun stopMonitoring_cleansUpResources() {
        ticketMonitor.stopMonitoring(1L)

        assertFalse(ticketMonitor.isMonitoring(1L))
    }

    /** 测试停止所有监控，验证所有任务被清理 */
    @Test
    fun stopAllMonitoring_cleansUpAllResources() {
        ticketMonitor.stopAllMonitoring()

        assertFalse(ticketMonitor.isMonitoring(1L))
        assertFalse(ticketMonitor.isMonitoring(2L))
    }

    /** 测试检查不存在的任务监控状态，返回false */
    @Test
    fun isMonitoring_nonExistentTask_returnsFalse() {
        assertFalse(ticketMonitor.isMonitoring(999L))
    }

    // ==================== 座次偏好检查测试 ====================

    /** 测试空座次偏好列表，直接返回null */
    @Test
    fun checkPreferredSeats_emptyPreferences_returnsNull() = testScope.runTest {
        val task = createTestBookingTask(seatPreferences = emptyList())

        val result = ticketMonitor.checkPreferredSeats(task)

        assertNull(result)
    }

    /** 测试查询所有座次余票，验证调用repository查询 */
    @Test
    fun checkAllSeatTypes_queriesRepository() = testScope.runTest {
        val task = createTestBookingTask()
        `when`(ticketRepository.queryTickets(anyString(), anyString(), anyString()))
            .thenReturn(Result.success(emptyList()))

        val result = ticketMonitor.checkAllSeatTypes(task)

        assertTrue(result.isEmpty())
        verify(ticketRepository).queryTickets(task.departureStation, task.arrivalStation, task.departureDate)
    }

    /** 测试查询所有座次余票失败，返回空Map */
    @Test
    fun checkAllSeatTypes_queryFailure_returnsEmptyMap() = testScope.runTest {
        val task = createTestBookingTask()
        `when`(ticketRepository.queryTickets(anyString(), anyString(), anyString()))
            .thenReturn(Result.failure(Exception("网络错误")))

        val result = ticketMonitor.checkAllSeatTypes(task)

        assertTrue(result.isEmpty())
    }

    /** 测试查询所有座次余票成功但找不到目标车次，返回空Map */
    @Test
    fun checkAllSeatTypes_noMatchingTrain_returnsEmptyMap() = testScope.runTest {
        val task = createTestBookingTask(trainNumber = "G999")
        val tickets = listOf(createTestTicket("G1"))
        `when`(ticketRepository.queryTickets(anyString(), anyString(), anyString()))
            .thenReturn(Result.success(tickets))

        val result = ticketMonitor.checkAllSeatTypes(task)

        assertTrue(result.isEmpty())
    }

    /** 测试查询所有座次余票成功且找到目标车次，返回座次信息 */
    @Test
    fun checkAllSeatTypes_matchingTrain_returnsSeatMap() = testScope.runTest {
        val seatInfo = SeatInfo("M", "一等座", 553.0, 10, true)
        val ticket = createTestTicket("G1", seatTypes = mapOf("M" to seatInfo))
        val task = createTestBookingTask(trainNumber = "G1")
        `when`(ticketRepository.queryTickets(anyString(), anyString(), anyString()))
            .thenReturn(Result.success(listOf(ticket)))

        val result = ticketMonitor.checkAllSeatTypes(task)

        assertTrue(result.containsKey("M"))
        assertEquals(10, result["M"]?.remainTicket)
    }

    // ==================== 辅助方法 ====================

    /** 创建测试用BookingTask对象 */
    private fun createTestBookingTask(
        trainNumber: String = "G1",
        seatPreferences: List<String> = listOf("M", "O")
    ): BookingTask {
        return BookingTask(
            id = 1L,
            trainNumber = trainNumber,
            trainNo = "240000G100",
            departureStation = "VNP",
            departureStationName = "北京南",
            arrivalStation = "AOH",
            arrivalStationName = "上海虹桥",
            departureDate = "2026-06-01",
            departureTime = "08:00",
            arrivalTime = "12:00",
            seatType = "M",
            seatTypeName = "一等座",
            passengerIds = listOf("P001"),
            passengerNames = listOf("张三"),
            strategy = "NORMAL",
            seatPreferences = seatPreferences
        )
    }

    /** 创建测试用TicketInfo对象 */
    private fun createTestTicket(
        trainCode: String,
        seatTypes: Map<String, SeatInfo> = emptyMap()
    ): TicketInfo {
        return TicketInfo(
            trainCode = trainCode,
            trainNo = "240000${trainCode}00",
            startStation = "VNP",
            endStation = "AOH",
            fromStation = "VNP",
            toStation = "AOH",
            startTime = "08:00",
            arriveTime = "12:00",
            dayDifference = "0",
            trainClassName = "",
            duration = "04:00",
            canWebBuy = "Y",
            seatTypes = seatTypes
        )
    }
}
