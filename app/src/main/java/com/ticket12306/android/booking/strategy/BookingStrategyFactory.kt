package com.ticket12306.android.booking.strategy

import com.ticket12306.android.data.model.BookingStrategyType as ModelStrategyType

/**
 * 抢票策略工厂
 * 根据策略类型创建对应的策略实例
 */
object BookingStrategyFactory {

    private val strategies = mutableMapOf<ModelStrategyType, BookingStrategy>()

    /**
     * 获取指定类型的策略实例（单例缓存）
     * 步骤：
     * 1. 从缓存中查找
     * 2. 未找到则创建新实例并缓存
     */
    fun getStrategy(type: ModelStrategyType): BookingStrategy {
        return strategies.getOrPut(type) {
            when (type) {
                ModelStrategyType.NORMAL -> NormalStrategy()
                ModelStrategyType.HIGH_SPEED -> HighSpeedStrategy()
                ModelStrategyType.EXTREME -> ExtremeStrategy()
                ModelStrategyType.SMART -> SmartStrategy()
            }
        }
    }
}
