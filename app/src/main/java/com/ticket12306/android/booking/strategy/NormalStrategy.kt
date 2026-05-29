package com.ticket12306.android.booking.strategy

import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.TicketCheckResult

/**
 * 普通策略：固定间隔查询
 * - 按用户设定的刷新间隔固定查询
 * - 有票即下单
 * - 适合日常抢票场景
 */
class NormalStrategy : BookingStrategy {

    override val type: BookingStrategyType = BookingStrategyType.NORMAL

    /**
     * 计算查询间隔
     * 步骤：
     * 1. 使用任务配置的固定刷新间隔
     * 2. 转换为毫秒返回
     */
    override fun calculateQueryInterval(
        task: BookingTask,
        lastCheckResult: TicketCheckResult?,
        consecutiveNoTicketCount: Int
    ): Long {
        return task.refreshInterval * 1000L
    }

    /**
     * 判断是否满足抢票条件
     * 步骤：
     * 1. 检查是否有票
     * 2. 如果不接受候补，则必须有可用余票
     */
    override fun shouldBookNow(checkResult: TicketCheckResult, task: BookingTask): Boolean {
        if (!checkResult.hasTicket) return false
        if (!task.acceptWaitlist) {
            val remainTicket = checkResult.seatInfo?.remainTicket ?: 0
            if (remainTicket <= 0) return false
        }
        return true
    }

    override fun getConcurrentQueryCount(): Int = 1

    override fun getBookingTimeout(): Long = 30_000L
}
