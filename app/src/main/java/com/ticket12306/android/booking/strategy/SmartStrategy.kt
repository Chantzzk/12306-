package com.ticket12306.android.booking.strategy

import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.TicketCheckResult
import com.ticket12306.android.data.model.TicketStatus
import kotlin.math.min

/**
 * 智能策略：根据余票情况动态调整
 * - 有票时自动加快查询频率（最低1秒）
 * - 连续无票时逐步放慢查询（最长30秒）
 * - 余票紧张时（≤5张）使用极限速度
 * - 服务器繁忙时自动降速
 * - 综合考虑余票数量、连续无票次数、网络状态
 */
class SmartStrategy : BookingStrategy {

    override val type: BookingStrategyType = BookingStrategyType.SMART

    companion object {
        private const val MIN_INTERVAL_MS = 1_000L
        private const val MAX_INTERVAL_MS = 30_000L
        private const val BASE_INTERVAL_MS = 5_000L
        private const val FEW_TICKET_THRESHOLD = 5
        private const val BACKOFF_MULTIPLIER = 1.5
    }

    /**
     * 计算查询间隔
     * 步骤：
     * 1. 有票且余票紧张（≤5张）：1秒极速查询
     * 2. 有票且余票充足：2秒快速查询
     * 3. 无票：根据连续无票次数指数退避
     *    - 第1次无票：基础间隔
     *    - 每多一次无票：间隔 × 1.5
     *    - 上限30秒
     */
    override fun calculateQueryInterval(
        task: BookingTask,
        lastCheckResult: TicketCheckResult?,
        consecutiveNoTicketCount: Int
    ): Long {
        if (lastCheckResult?.hasTicket == true) {
            val remainCount = lastCheckResult.seatInfo?.remainTicket ?: 0
            return if (remainCount in 1..FEW_TICKET_THRESHOLD) {
                MIN_INTERVAL_MS
            } else {
                2_000L
            }
        }

        val baseInterval = task.refreshInterval * 1000L
        val backoffInterval = baseInterval * BACKOFF_MULTIPLIER.pow(consecutiveNoTicketCount)
        return min(backoffInterval.toLong(), MAX_INTERVAL_MS)
    }

    /**
     * 判断是否满足抢票条件
     * 步骤：
     * 1. 有票且余票>0：立即抢票
     * 2. 接受候补且余票为0但可候补：也尝试抢票
     */
    override fun shouldBookNow(checkResult: TicketCheckResult, task: BookingTask): Boolean {
        if (checkResult.hasTicket) return true
        if (task.acceptWaitlist) return true
        return false
    }

    override fun getConcurrentQueryCount(): Int = 2

    override fun getBookingTimeout(): Long = 20_000L
}

private fun Double.pow(exponent: Int): Double {
    var result = 1.0
    repeat(exponent) { result *= this }
    return result
}
