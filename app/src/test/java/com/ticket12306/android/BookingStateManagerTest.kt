package com.ticket12306.android

import com.ticket12306.android.booking.BookingManager
import com.ticket12306.android.booking.BookingStateManager
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.BookingStatus
import com.ticket12306.android.data.model.BookingTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
 * BookingStateManager 单元测试
 * 测试范围：
 * 1. 任务启动（验证任务存在性、激活、状态更新）
 * 2. 任务停止（停止抢票、停用任务、状态更新）
 * 3. 任务重置（重置重试计数和状态）
 * 4. 任务删除（停止抢票、删除数据、移除缓存）
 * 5. 状态查询（缓存优先、数据库回退）
 * 6. 批量操作（启动所有活跃任务、停止所有任务）
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BookingStateManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Mock
    private lateinit var bookingTaskDao: BookingTaskDao

    @Mock
    private lateinit var bookingLogDao: BookingLogDao

    @Mock
    private lateinit var bookingManager: BookingManager

    private lateinit var stateManager: BookingStateManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        `when`(bookingLogDao.insertLog(any())).thenReturn(1L)
        stateManager = BookingStateManager(bookingTaskDao, bookingLogDao, bookingManager)
    }

    // ==================== 任务启动测试 ====================

    /** 测试启动不存在的任务，应直接返回不执行任何操作 */
    @Test
    fun startTask_nonExistentTask_doesNotStartBooking() = testScope.runTest {
        `when`(bookingTaskDao.getBookingTaskById(anyLong())).thenReturn(null)

        stateManager.startTask(createTestBookingTask(id = 999L))
        advanceUntilIdle()

        verify(bookingManager, never()).startAutoBooking(any(), any())
    }

    /** 测试启动存在的任务，应激活任务并启动自动抢票 */
    @Test
    fun startTask_existingTask_activatesAndStartsBooking() = testScope.runTest {
        val task = createTestBookingTask(id = 1L)
        `when`(bookingTaskDao.getBookingTaskById(1L)).thenReturn(task)
        `when`(bookingTaskDao.activateBookingTask(1L)).thenReturn(Unit)
        `when`(bookingTaskDao.updateTaskStatus(eq(1L), anyString())).thenReturn(Unit)

        stateManager.startTask(task)
        advanceUntilIdle()

        verify(bookingTaskDao).activateBookingTask(1L)
        verify(bookingManager).startAutoBooking(any(), eq(task))
    }

    // ==================== 任务停止测试 ====================

    /** 测试停止任务，应停止抢票、停用任务、更新状态为已取消 */
    @Test
    fun stopTask_stopsBookingAndDeactivates() = testScope.runTest {
        `when`(bookingTaskDao.deactivateBookingTask(anyLong())).thenReturn(Unit)
        `when`(bookingTaskDao.updateTaskStatus(eq(1L), anyString())).thenReturn(Unit)

        stateManager.stopTask(1L)
        advanceUntilIdle()

        verify(bookingManager).stopAutoBooking(1L)
        verify(bookingTaskDao).deactivateBookingTask(1L)
        assertEquals(BookingStatus.CANCELLED, stateManager.taskStates.value[1L])
    }

    // ==================== 任务重置测试 ====================

    /** 测试重置任务，应停止抢票并重置状态为等待中 */
    @Test
    fun resetTask_stopsBookingAndResetsState() = testScope.runTest {
        `when`(bookingTaskDao.resetTask(anyLong())).thenReturn(Unit)
        `when`(bookingTaskDao.updateTaskStatus(eq(1L), anyString())).thenReturn(Unit)

        stateManager.resetTask(1L)
        advanceUntilIdle()

        verify(bookingManager).stopAutoBooking(1L)
        verify(bookingTaskDao).resetTask(1L)
        assertEquals(BookingStatus.PENDING, stateManager.taskStates.value[1L])
    }

    // ==================== 任务删除测试 ====================

    /** 测试删除任务，应停止抢票、删除数据、移除缓存 */
    @Test
    fun deleteTask_stopsBookingAndDeletesTask() = testScope.runTest {
        `when`(bookingTaskDao.deleteBookingTaskById(anyLong())).thenReturn(Unit)

        stateManager.deleteTask(1L)
        advanceUntilIdle()

        verify(bookingManager).stopAutoBooking(1L)
        verify(bookingTaskDao).deleteBookingTaskById(1L)
        assertNull(stateManager.taskStates.value[1L])
    }

    // ==================== 状态查询测试 ====================

    /** 测试从缓存获取任务状态 */
    @Test
    fun getTaskStatus_fromCache_returnsCachedStatus() = testScope.runTest {
        `when`(bookingTaskDao.updateTaskStatus(eq(1L), anyString())).thenReturn(Unit)
        stateManager.stopTask(1L)
        advanceUntilIdle()

        val status = stateManager.getTaskStatus(1L)
        assertEquals(BookingStatus.CANCELLED, status)
    }

    /** 测试从数据库获取不存在的任务状态，返回已取消 */
    @Test
    fun getTaskStatus_nonExistentTask_returnsCancelled() = testScope.runTest {
        `when`(bookingTaskDao.getBookingTaskById(anyLong())).thenReturn(null)

        val status = stateManager.getTaskStatus(999L)
        assertEquals(BookingStatus.CANCELLED, status)
    }

    /** 测试从数据库获取任务状态，解析无效状态字符串时返回PENDING */
    @Test
    fun getTaskStatus_invalidStatus_returnsPending() = testScope.runTest {
        val task = createTestBookingTask(id = 1L).copy(status = "INVALID_STATUS")
        `when`(bookingTaskDao.getBookingTaskById(1L)).thenReturn(task)

        val status = stateManager.getTaskStatus(1L)
        assertEquals(BookingStatus.PENDING, status)
    }

    // ==================== 批量操作测试 ====================

    /** 测试停止所有任务，验证状态缓存被清空 */
    @Test
    fun stopAllTasks_clearsStateCache() {
        stateManager.stopAllTasks()

        verify(bookingManager).stopAllAutoBooking()
        assertTrue(stateManager.taskStates.value.isEmpty())
    }

    /** 测试批量启动所有活跃任务 */
    @Test
    fun startAllActiveTasks_startsEachActiveTask() = testScope.runTest {
        val tasks = listOf(
            createTestBookingTask(id = 1L),
            createTestBookingTask(id = 2L)
        )
        val tasksFlow = MutableStateFlow(tasks)
        `when`(bookingTaskDao.getActiveBookingTasks()).thenReturn(tasksFlow)
        `when`(bookingTaskDao.getBookingTaskById(anyLong())).thenAnswer { invocation ->
            val id = invocation.getArgument<Long>(0)
            tasks.find { it.id == id }
        }
        `when`(bookingTaskDao.activateBookingTask(anyLong())).thenReturn(Unit)
        `when`(bookingTaskDao.updateTaskStatus(anyLong(), anyString())).thenReturn(Unit)

        stateManager.startAllActiveTasks()
        advanceUntilIdle()

        verify(bookingManager, atLeastOnce()).startAutoBooking(any(), any())
    }

    // ==================== 辅助方法 ====================

    /** 创建测试用BookingTask对象 */
    private fun createTestBookingTask(id: Long = 1L): BookingTask {
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
            strategy = "NORMAL"
        )
    }
}
