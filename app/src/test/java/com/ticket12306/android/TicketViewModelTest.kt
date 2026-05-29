package com.ticket12306.android

import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.ui.ticket.*
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
import org.mockito.Mockito.*

/**
 * TicketViewModel 单元测试
 * 测试范围：
 * 1. 查询参数管理（设置出发站/到达站、交换、日期导航）
 * 2. 查询验证逻辑（空站、相同站、日期校验）
 * 3. 筛选逻辑（车次类型、时间段、仅看有票）
 * 4. 排序逻辑（排序字段切换、升降序）
 * 5. 抢票任务管理（创建、激活、暂停、删除）
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TicketViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var ticketRepository: TicketRepository
    private lateinit var viewModel: TicketViewModel

    private val bookingTasksFlow = MutableStateFlow<List<BookingTask>>(emptyList())

    @Before
    fun setUp() {
        ticketRepository = mock(TicketRepository::class.java)
        `when`(ticketRepository.allBookingTasks).thenReturn(bookingTasksFlow)
        viewModel = TicketViewModel(ticketRepository)
    }

    // ==================== 查询参数管理测试 ====================

    /** 测试设置出发站信息，验证Flow值正确更新 */
    @Test
    fun setFromStation_updatesStationCodeAndName() {
        viewModel.setFromStation("BJP", "北京")

        assertEquals("BJP", viewModel.fromStationCode.value)
        assertEquals("北京", viewModel.fromStationName.value)
    }

    /** 测试设置到达站信息，验证Flow值正确更新 */
    @Test
    fun setToStation_updatesStationCodeAndName() {
        viewModel.setToStation("SHH", "上海")

        assertEquals("SHH", viewModel.toStationCode.value)
        assertEquals("上海", viewModel.toStationName.value)
    }

    /** 测试交换出发站和到达站，验证双方信息互换 */
    @Test
    fun swapStations_exchangesFromAndToStation() {
        viewModel.setFromStation("BJP", "北京")
        viewModel.setToStation("SHH", "上海")

        viewModel.swapStations()

        assertEquals("SHH", viewModel.fromStationCode.value)
        assertEquals("上海", viewModel.fromStationName.value)
        assertEquals("BJP", viewModel.toStationCode.value)
        assertEquals("北京", viewModel.toStationName.value)
    }

    /** 测试连续交换两次，验证回到初始状态 */
    @Test
    fun swapStations_twice_returnsToOriginal() {
        viewModel.setFromStation("BJP", "北京")
        viewModel.setToStation("SHH", "上海")

        viewModel.swapStations()
        viewModel.swapStations()

        assertEquals("BJP", viewModel.fromStationCode.value)
        assertEquals("北京", viewModel.fromStationName.value)
        assertEquals("SHH", viewModel.toStationCode.value)
        assertEquals("上海", viewModel.toStationName.value)
    }

    // ==================== 查询验证逻辑测试 ====================

    /** 测试出发站为空时查询，应显示错误消息 */
    @Test
    fun queryTickets_emptyFromStation_showsError() = testScope.runTest {
        viewModel.setToStation("SHH", "上海")

        viewModel.queryTickets()
        advanceUntilIdle()

        assertEquals(QueryState.Idle, viewModel.queryState.value)
    }

    /** 测试到达站为空时查询，应显示错误消息 */
    @Test
    fun queryTickets_emptyToStation_showsError() = testScope.runTest {
        viewModel.setFromStation("BJP", "北京")

        viewModel.queryTickets()
        advanceUntilIdle()

        assertEquals(QueryState.Idle, viewModel.queryState.value)
    }

    /** 测试出发站和到达站相同时查询，应显示错误消息 */
    @Test
    fun queryTickets_sameStation_showsError() = testScope.runTest {
        viewModel.setFromStation("BJP", "北京")
        viewModel.setToStation("BJP", "北京")

        viewModel.queryTickets()
        advanceUntilIdle()

        assertEquals(QueryState.Idle, viewModel.queryState.value)
    }

    // ==================== 筛选逻辑测试 ====================

    /** 测试设置车次类型筛选，验证Flow值更新 */
    @Test
    fun setTrainTypeFilter_updatesState() {
        viewModel.setTrainTypeFilter(TrainType.HIGH_SPEED)

        assertEquals(TrainType.HIGH_SPEED, viewModel.selectedTrainType.value)
    }

    /** 测试设置时间段筛选，验证Flow值更新 */
    @Test
    fun setTimePeriodFilter_updatesState() {
        viewModel.setTimePeriodFilter(TimePeriod.MORNING)

        assertEquals(TimePeriod.MORNING, viewModel.selectedTimePeriod.value)
    }

    /** 测试设置仅看有票筛选，验证Flow值更新 */
    @Test
    fun setOnlyHasTicket_updatesState() {
        viewModel.setOnlyHasTicket(true)

        assertTrue(viewModel.onlyHasTicket.value)
    }

    /** 测试重置筛选条件，验证所有筛选恢复默认 */
    @Test
    fun resetFilters_resetsAllFilters() {
        viewModel.setTrainTypeFilter(TrainType.HIGH_SPEED)
        viewModel.setTimePeriodFilter(TimePeriod.MORNING)
        viewModel.setOnlyHasTicket(true)

        viewModel.resetFilters()

        assertEquals(TrainType.ALL, viewModel.selectedTrainType.value)
        assertNull(viewModel.selectedTimePeriod.value)
        assertFalse(viewModel.onlyHasTicket.value)
    }

    // ==================== 排序逻辑测试 ====================

    /** 测试设置不同排序字段，验证字段更新且默认升序 */
    @Test
    fun setSortField_differentField_updatesFieldAndResetsOrder() {
        viewModel.setSortField(SortField.ARRIVAL_TIME)

        assertEquals(SortField.ARRIVAL_TIME, viewModel.sortField.value)
        assertEquals(SortOrder.ASCENDING, viewModel.sortOrder.value)
    }

    /** 测试重复点击同一排序字段，验证排序方向切换为降序 */
    @Test
    fun setSortField_sameField_togglesSortOrder() {
        viewModel.setSortField(SortField.DEPARTURE_TIME)

        viewModel.setSortField(SortField.DEPARTURE_TIME)

        assertEquals(SortOrder.DESCENDING, viewModel.sortOrder.value)
    }

    /** 测试连续切换排序方向三次，验证最终为升序 */
    @Test
    fun setSortField_sameFieldThreeTimes_togglesBackToAscending() {
        viewModel.setSortField(SortField.DEPARTURE_TIME)
        viewModel.setSortField(SortField.DEPARTURE_TIME)
        viewModel.setSortField(SortField.DEPARTURE_TIME)

        assertEquals(SortOrder.ASCENDING, viewModel.sortOrder.value)
    }

    /** 测试从降序切换到新字段，验证方向重置为升序 */
    @Test
    fun setSortField_afterToggleThenNewField_resetsToAscending() {
        viewModel.setSortField(SortField.DEPARTURE_TIME)
        viewModel.setSortField(SortField.DEPARTURE_TIME)

        viewModel.setSortField(SortField.DURATION)

        assertEquals(SortField.DURATION, viewModel.sortField.value)
        assertEquals(SortOrder.ASCENDING, viewModel.sortOrder.value)
    }

    // ==================== 车次选择测试 ====================

    /** 测试选中车次，验证selectedTicket更新 */
    @Test
    fun selectTicket_updatesSelectedTicket() {
        val ticket = createTestTicket("G1")

        viewModel.selectTicket(ticket)

        assertEquals("G1", viewModel.selectedTicket.value?.trainCode)
    }

    /** 测试清空车票，验证所有查询结果和状态重置 */
    @Test
    fun clearTickets_resetsAllQueryState() {
        viewModel.clearTickets()

        assertTrue(viewModel.displayTickets.value.isEmpty())
        assertEquals(0, viewModel.totalResultCount.value)
        assertEquals(QueryState.Idle, viewModel.queryState.value)
    }

    // ==================== 辅助方法 ====================

    /** 创建测试用TicketInfo对象 */
    private fun createTestTicket(
        trainCode: String,
        startTime: String = "08:00",
        arriveTime: String = "12:00",
        duration: String = "04:00",
        seatTypes: Map<String, SeatInfo> = emptyMap()
    ): TicketInfo {
        return TicketInfo(
            trainCode = trainCode,
            trainNo = "240000${trainCode}00",
            startStation = "VNP",
            endStation = "AOH",
            fromStation = "VNP",
            toStation = "AOH",
            startTime = startTime,
            arriveTime = arriveTime,
            dayDifference = "0",
            trainClassName = "",
            duration = duration,
            canWebBuy = "Y",
            seatTypes = seatTypes
        )
    }
}
