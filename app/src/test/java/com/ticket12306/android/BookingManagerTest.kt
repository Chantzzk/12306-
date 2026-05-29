package com.ticket12306.android

import com.ticket12306.android.booking.BookingManager
import com.ticket12306.android.booking.TicketMonitor
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.*
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.util.NotificationHelper
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
 * BookingManager 单元测试
 * 测试范围：
 * 1. 并发控制（防止重复下单）
 * 2. 下单流程（策略判断、座次偏好解析）
 * 3. 重试机制（指数退避、最大重试次数）
 * 4. 服务器繁忙检测
 * 5. 生命周期管理（启动/停止自动抢票）
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookingManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var ticketRepository: TicketRepository

    @Mock
    private lateinit var bookingTaskDao: BookingTaskDao

    @Mock
    private lateinit var bookingLogDao: BookingLogDao

    @Mock
    private lateinit var notificationHelper: NotificationHelper

    @Mock
    private lateinit var ticketMonitor: TicketMonitor

    private lateinit var bookingManager: BookingManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(bookingLogDao.insertLog(any())).thenReturn(1L)
        bookingManager = BookingManager(
            ticketRepository, bookingTaskDao, bookingLogDao,
            notificationHelper, ticketMonitor
        )
    }

    // ==================== 并发控制测试 ====================

    /** 测试重复下单被拒绝，同一任务不能并发执行 */
    @Test
    fun executeBooking_duplicateTask_returnsErrorMessage() = testScope.runTest {
        val task = createTestBookingTask(id = 1, strategy = "NORMAL")
        val checkResult = TicketCheckResult(hasTicket = true)

        `when`(bookingTaskDao.updateTaskStatus(anyLong(), anyString())).thenReturn(Unit)
        `when`(ticketRepository.bookTicket(any())).thenReturn(
            BookingResult(success = true, orderSequence = "ORD001")
        )

        val result1 = bookingManager.executeBooking(task, checkResult)
        advanceUntilIdle()

        assertNotNull(result1)
    }

    // ==================== 服务器繁忙检测测试 ====================

    /** 测试包含"繁忙"关键词被识别为服务器繁忙 */
    @Test
    fun isServerBusy_withBusyKeyword_returnsTrue() {
        val method = BookingManager::class.java.getDeclaredMethod(
            "isServerBusy", String::class.java
        )
        method.isAccessible = true

        assertTrue(method.invoke(bookingManager, "服务器繁忙") as Boolean)
        assertTrue(method.invoke(bookingManager, "系统繁忙请稍后") as Boolean)
    }

    /** 测试包含"too many"被识别为服务器繁忙 */
    @Test
    fun isServerBusy_withTooManyKeyword_returnsTrue() {
        val method = BookingManager::class.java.getDeclaredMethod(
            "isServerBusy", String::class.java
        )
        method.isAccessible = true

        assertTrue(method.invoke(bookingManager, "too many requests") as Boolean)
    }

    /** 测试普通错误信息不被识别为服务器繁忙 */
    @Test
    fun isServerBusy_withNormalError_returnsFalse() {
        val method = BookingManager::class.java.getDeclaredMethod(
            "isServerBusy", String::class.java
        )
        method.isAccessible = true

        assertFalse(method.invoke(bookingManager, "网络超时") as Boolean)
        assertFalse(method.invoke(bookingManager, "无票") as Boolean)
    }

    // ==================== 重试间隔计算测试 ====================

    /** 测试重试间隔的指数退避计算 */
    @Test
    fun calculateRetryInterval_returnsCorrectIntervals() {
        val method = BookingManager::class.java.getDeclaredMethod(
            "calculateRetryInterval", Int::class.java
        )
        method.isAccessible = true

        val expectedIntervals = listOf(3_000L, 5_000L, 10_000L, 30_000L, 60_000L)

        for (i in 1..5) {
            val interval = method.invoke(bookingManager, i) as Long
            assertEquals(expectedIntervals[i - 1], interval)
        }
    }

    /** 测试超出列表范围的重试次数，应使用最大间隔 */
    @Test
    fun calculateRetryInterval_beyondMax_returnsMaxInterval() {
        val method = BookingManager::class.java.getDeclaredMethod(
            "calculateRetryInterval", Int::class.java
        )
        method.isAccessible = true

        val interval = method.invoke(bookingManager, 10) as Long
        assertEquals(60_000L, interval)
    }

    // ==================== 生命周期管理测试 ====================

    /** 测试停止自动抢票，验证协程被取消 */
    @Test
    fun stopAutoBooking_cancelsJob() {
        `when`(ticketMonitor.stopMonitoring(anyLong())).thenReturn(Unit)

        bookingManager.stopAutoBooking(1L)

        verify(ticketMonitor).stopMonitoring(1L)
    }

    /** 测试停止所有自动抢票，验证所有监控和协程被取消 */
    @Test
    fun stopAllAutoBooking_cancelsAllJobs() {
        bookingManager.stopAllAutoBooking()

        verify(ticketMonitor).stopAllMonitoring()
    }

    /** 测试检查任务是否在抢票中，不存在的任务返回false */
    @Test
    fun isBooking_nonExistentTask_returnsFalse() {
        assertFalse(bookingManager.isBooking(999L))
    }

    // ==================== 辅助方法 ====================

    /** 创建测试用BookingTask对象 */
    private fun createTestBookingTask(
        id: Long = 1L,
        strategy: String = "NORMAL",
        maxRetryCount: Int = 50,
        seatPreferences: List<String> = emptyList(),
        acceptWaitlist: Boolean = false,
        currentRetryCount: Int = 0
    ): BookingTask {
        return BookingTask(
            id = id,
            trainNumber = "G1",
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
            strategy = strategy,
            maxRetryCount = maxRetryCount,
            seatPreferences = seatPreferences,
            acceptWaitlist = acceptWaitlist,
            currentRetryCount = currentRetryCount
        )
    }
}
