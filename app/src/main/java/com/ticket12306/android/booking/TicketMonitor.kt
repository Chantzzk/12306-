package com.ticket12306.android.booking

import com.ticket12306.android.booking.strategy.BookingStrategyFactory
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingStrategyType
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.LogType
import com.ticket12306.android.data.model.SeatInfo
import com.ticket12306.android.data.model.TicketCheckResult
import com.ticket12306.android.data.model.TicketInfo
import com.ticket12306.android.data.repository.TicketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 余票监控器
 * 核心职责：
 * 1. 定时查询余票信息
 * 2. 比对余票变化
 * 3. 判断是否满足抢票条件
 * 4. 支持多车次同时监控
 * 5. 查询间隔自适应（有票时加快，无票时放慢）
 */
@Singleton
class TicketMonitor @Inject constructor(
    private val ticketRepository: TicketRepository,
    private val bookingTaskDao: BookingTaskDao,
    private val bookingLogDao: BookingLogDao
) {

    private val _monitorResults = MutableStateFlow<Map<Long, TicketCheckResult>>(emptyMap())
    val monitorResults: StateFlow<Map<Long, TicketCheckResult>> = _monitorResults.asStateFlow()

    private val _ticketChanges = MutableStateFlow<List<TicketChangeEvent>>(emptyList())
    val ticketChanges: StateFlow<List<TicketChangeEvent>> = _ticketChanges.asStateFlow()

    private val monitoringJobs = mutableMapOf<Long, Job>()
    private val consecutiveNoTicketCounts = mutableMapOf<Long, Int>()
    private val lastCheckResults = mutableMapOf<Long, TicketCheckResult>()

    /**
     * 启动单个任务的余票监控
     * 步骤：
     * 1. 取消该任务已有的监控
     * 2. 在协程中循环查询余票
     * 3. 根据策略计算查询间隔
     * 4. 比对余票变化并通知
     * 5. 判断是否满足抢票条件
     */
    fun startMonitoring(scope: CoroutineScope, task: BookingTask, onTicketAvailable: (TicketCheckResult) -> Unit) {
        stopMonitoring(task.id)

        consecutiveNoTicketCounts[task.id] = 0

        val job = scope.launch {
            val strategy = BookingStrategyFactory.getStrategy(
                BookingStrategyType.valueOf(task.strategy)
            )

            while (isActive) {
                try {
                    val checkResults = performCheck(task, strategy)
                    val bestResult = selectBestResult(checkResults, task)

                    processCheckResult(task, bestResult, onTicketAvailable)

                    val interval = strategy.calculateQueryInterval(
                        task,
                        bestResult,
                        consecutiveNoTicketCounts[task.id] ?: 0
                    )
                    delay(interval)
                } catch (e: Exception) {
                    logResult(task.id, LogType.ERROR, "监控异常: ${e.message}")
                    delay(5_000L)
                }
            }
        }

        monitoringJobs[task.id] = job
        scope.launch {
            logResult(task.id, LogType.STATUS_CHANGE, "开始监控: ${task.trainNumber}")
        }
    }

    /**
     * 执行余票查询（支持并发）
     * 步骤：
     * 1. 获取策略的并发查询数
     * 2. 并发发起多个查询请求
     * 3. 收集所有查询结果
     */
    private suspend fun performCheck(task: BookingTask, strategy: com.ticket12306.android.booking.strategy.BookingStrategy): List<TicketCheckResult> {
        val concurrentCount = strategy.getConcurrentQueryCount()

        return coroutineScope {
            val deferreds = (1..concurrentCount).map {
                async { ticketRepository.checkTicketAvailability(task) }
            }
            deferreds.awaitAll()
        }
    }

    /**
     * 从多个查询结果中选择最优结果
     * 步骤：
     * 1. 优先选择有票的结果
     * 2. 多个有票结果中选择余票最多的
     * 3. 无票结果中选择错误信息最少的
     */
    private fun selectBestResult(results: List<TicketCheckResult>, task: BookingTask): TicketCheckResult {
        val availableResults = results.filter { it.hasTicket }
        if (availableResults.isNotEmpty()) {
            return availableResults.maxByOrNull { it.seatInfo?.remainTicket ?: 0 } ?: results.first()
        }
        return results.firstOrNull() ?: TicketCheckResult(hasTicket = false)
    }

    /**
     * 处理查询结果
     * 步骤：
     * 1. 更新监控结果缓存
     * 2. 比对余票变化
     * 3. 更新连续无票计数
     * 4. 有票时触发回调
     * 5. 记录日志
     */
    private suspend fun processCheckResult(
        task: BookingTask,
        result: TicketCheckResult,
        onTicketAvailable: (TicketCheckResult) -> Unit
    ) {
        _monitorResults.value = _monitorResults.value.toMutableMap().apply {
            this[task.id] = result
        }

        checkTicketChange(task, result)

        if (result.hasTicket) {
            consecutiveNoTicketCounts[task.id] = 0
            logResult(
                task.id, LogType.QUERY,
                "有余票: ${task.trainNumber} ${result.seatInfo?.seatTypeName ?: ""} 剩余${result.seatInfo?.remainTicket ?: 0}张"
            )
            onTicketAvailable(result)
        } else {
            val count = (consecutiveNoTicketCounts[task.id] ?: 0) + 1
            consecutiveNoTicketCounts[task.id] = count
            if (count % 10 == 0) {
                logResult(task.id, LogType.QUERY, "已查询${count}次，暂无余票: ${task.trainNumber}")
            }
        }

        lastCheckResults[task.id] = result
    }

    /**
     * 比对余票变化
     * 步骤：
     * 1. 获取上次查询结果
     * 2. 比较余票数量变化
     * 3. 记录变化事件
     */
    private suspend fun checkTicketChange(task: BookingTask, currentResult: TicketCheckResult) {
        val lastResult = lastCheckResults[task.id]
        if (lastResult != null) {
            val lastCount = lastResult.seatInfo?.remainTicket ?: 0
            val currentCount = currentResult.seatInfo?.remainTicket ?: 0

            if (lastCount != currentCount) {
                val change = TicketChangeEvent(
                    taskId = task.id,
                    trainNumber = task.trainNumber,
                    seatType = task.seatTypeName,
                    previousCount = lastCount,
                    currentCount = currentCount,
                    timestamp = System.currentTimeMillis()
                )
                _ticketChanges.value = _ticketChanges.value + change

                val direction = if (currentCount > lastCount) "增加" else "减少"
                logResult(
                    task.id, LogType.QUERY,
                    "余票${direction}: ${lastCount}→${currentCount} (${task.seatTypeName})"
                )
            }
        }
    }

    /** 停止指定任务的监控 */
    fun stopMonitoring(taskId: Long) {
        monitoringJobs[taskId]?.cancel()
        monitoringJobs.remove(taskId)
        consecutiveNoTicketCounts.remove(taskId)
        lastCheckResults.remove(taskId)
    }

    /** 停止所有监控 */
    fun stopAllMonitoring() {
        monitoringJobs.values.forEach { it.cancel() }
        monitoringJobs.clear()
        consecutiveNoTicketCounts.clear()
        lastCheckResults.clear()
    }

    /** 检查指定任务是否正在监控中 */
    fun isMonitoring(taskId: Long): Boolean {
        return monitoringJobs[taskId]?.isActive == true
    }

    /**
     * 查询指定车次的所有座次余票
     * 步骤：
     * 1. 查询车次信息
     * 2. 找到目标车次
     * 3. 返回所有座次的余票信息
     */
    suspend fun checkAllSeatTypes(task: BookingTask): Map<String, SeatInfo> {
        val result = ticketRepository.queryTickets(
            fromStation = task.departureStation,
            toStation = task.arrivalStation,
            date = task.departureDate
        )

        return result.fold(
            onSuccess = { tickets ->
                val targetTicket = tickets.find { it.trainCode == task.trainNumber }
                targetTicket?.seatTypes ?: emptyMap()
            },
            onFailure = { emptyMap() }
        )
    }

    /**
     * 检查座次偏好列表中是否有可用座次
     * 步骤：
     * 1. 查询所有座次余票
     * 2. 过滤出偏好座次中有票的
     * 3. 返回最优座次信息
     */
    suspend fun checkPreferredSeats(task: BookingTask): TicketCheckResult? {
        if (task.seatPreferences.isEmpty()) return null

        val allSeats = checkAllSeatTypes(task)
        val preferredAvailable = task.seatPreferences.mapNotNull { seatType ->
            allSeats[seatType]?.let { seatInfo ->
                if (seatInfo.remainTicket > 0 || task.acceptWaitlist) {
                    seatType to seatInfo
                } else null
            }
        }

        if (preferredAvailable.isEmpty()) return null

        val (bestSeatType, bestSeatInfo) = preferredAvailable.maxByOrNull { it.second.remainTicket } ?: return null

        val ticketInfo = TicketInfo(
            trainCode = task.trainNumber,
            trainNo = task.trainNo,
            startStation = "",
            endStation = "",
            fromStation = task.departureStation,
            toStation = task.arrivalStation,
            startTime = task.departureTime,
            arriveTime = task.arrivalTime,
            dayDifference = "0",
            trainClassName = "",
            duration = "",
            canWebBuy = "Y",
            seatTypes = mapOf(bestSeatType to bestSeatInfo)
        )

        return TicketCheckResult(
            hasTicket = bestSeatInfo.remainTicket > 0,
            ticketInfo = ticketInfo,
            seatInfo = bestSeatInfo
        )
    }

    private suspend fun logResult(taskId: Long, type: LogType, message: String) {
        bookingLogDao.insertLog(
            BookingLog(taskId = taskId, type = type.name, message = message)
        )
    }
}

data class TicketChangeEvent(
    val taskId: Long,
    val trainNumber: String,
    val seatType: String,
    val previousCount: Int,
    val currentCount: Int,
    val timestamp: Long
)
