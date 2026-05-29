package com.ticket12306.android.booking.strategy

import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.TicketCheckResult

/**
 * 极限策略：多线程并发查询 + 下单
 * - 3个并发查询线程同时请求
 * - 极短查询间隔（500ms）
 * - 下单超时极短（10秒），快速失败快速重试
 * - 适合极热门车次的最后冲刺
 */
class ExtremeStrategy : BookingStrategy {

    override val type: BookingStrategyType = BookingStrategyType.EXTREME

    companion object {
        private const val EXTREME_INTERVAL_MS = 500L
        private const val NORMAL_INTERVAL_MS = 2_000L
    }

    /**
     * 计算查询间隔
     * 步骤：
     * 1. 有票时使用极限间隔（500ms）
     * 2. 无票时使用2秒间隔（仍然很快）
     */
    override fun calculateQueryInterval(
        task: BookingTask,
        lastCheckResult: TicketCheckResult?,
        consecutiveNoTicketCount: Int
    ): Long {
        if (lastCheckResult?.hasTicket == true) {
            return EXTREME_INTERVAL_MS
        }
        return NORMAL_INTERVAL_MS
    }

    /**
     * 判断是否满足抢票条件
     * 步骤：
     * 1. 有票即抢，不管是否候补
     */
    override fun shouldBookNow(checkResult: TicketCheckResult, task: BookingTask): Boolean {
        return checkResult.hasTicket || task.acceptWaitlist
    }

    override fun getConcurrentQueryCount(): Int = 3

    override fun getBookingTimeout(): Long = 10_000L
}
