package com.ticket12306.android.booking.strategy

import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.TicketCheckResult

/**
 * 抢票策略接口
 * 定义抢票过程中的核心行为：查询间隔计算、是否满足抢票条件判断、并发数控制
 */
interface BookingStrategy {

    /** 策略类型标识 */
    val type: BookingStrategyType

    /**
     * 计算下一次查询的间隔时间（毫秒）
     * @param task 当前抢票任务
     * @param lastCheckResult 上次查询结果
     * @param consecutiveNoTicketCount 连续无票次数
     * @return 下次查询的等待时间（毫秒）
     */
    fun calculateQueryInterval(
        task: BookingTask,
        lastCheckResult: TicketCheckResult?,
        consecutiveNoTicketCount: Int
    ): Long

    /**
     * 判断当前余票查询结果是否满足抢票条件
     * @param checkResult 余票查询结果
     * @param task 抢票任务配置
     * @return 是否应该立即下单
     */
    fun shouldBookNow(checkResult: TicketCheckResult, task: BookingTask): Boolean

    /** 并发查询数 */
    fun getConcurrentQueryCount(): Int = 1

    /** 下单超时时间（毫秒） */
    fun getBookingTimeout(): Long = 30_000L
}

enum class BookingStrategyType {
    NORMAL,
    HIGH_SPEED,
    EXTREME,
    SMART
}
