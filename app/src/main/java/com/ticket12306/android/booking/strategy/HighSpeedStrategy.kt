package com.ticket12306.android.booking.strategy

import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.TicketCheckResult

/**
 * 高速策略：短间隔查询 + 快速下单
 * - 有票时大幅缩短查询间隔（最快1秒）
 * - 无票时保持正常间隔
 * - 下单超时更短，快速失败重试
 * - 适合热门车次抢票
 */
class HighSpeedStrategy : BookingStrategy {

    override val type: BookingStrategyType = BookingStrategyType.HIGH_SPEED

    companion object {
        private const val FAST_INTERVAL_MS = 1_000L
        private const val NORMAL_INTERVAL_MULTIPLIER = 0.6
    }

    /**
     * 计算查询间隔
     * 步骤：
     * 1. 如果上次查询有票，使用极速间隔（1秒）
     * 2. 如果无票，使用配置间隔的60%（比普通策略更快）
     */
    override fun calculateQueryInterval(
        task: BookingTask,
        lastCheckResult: TicketCheckResult?,
        consecutiveNoTicketCount: Int
    ): Long {
        if (lastCheckResult?.hasTicket == true) {
            return FAST_INTERVAL_MS
        }
        return (task.refreshInterval * 1000L * NORMAL_INTERVAL_MULTIPLIER).toLong()
    }

    /**
     * 判断是否满足抢票条件
     * 步骤：
     * 1. 有票即抢，无需额外判断
     */
    override fun shouldBookNow(checkResult: TicketCheckResult, task: BookingTask): Boolean {
        return checkResult.hasTicket
    }

    override fun getConcurrentQueryCount(): Int = 2

    override fun getBookingTimeout(): Long = 15_000L
}
