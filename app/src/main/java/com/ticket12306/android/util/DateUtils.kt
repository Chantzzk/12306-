package com.ticket12306.android.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm"
    private const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_DISPLAY_FORMAT = "MM月dd日"
    private const val WEEK_DAY_FORMAT = "EEEE"

    private val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val timeFormat = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
    private val dateDisplayFormat = SimpleDateFormat(DATE_DISPLAY_FORMAT, Locale.getDefault())
    private val weekDayFormat = SimpleDateFormat(WEEK_DAY_FORMAT, Locale.getDefault())

    /** 格式化日期为 yyyy-MM-dd 格式 */
    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    /** 格式化时间为 HH:mm 格式 */
    fun formatTime(date: Date): String {
        return timeFormat.format(date)
    }

    /** 格式化日期时间为 yyyy-MM-dd HH:mm:ss 格式 */
    fun formatDateTime(date: Date): String {
        return dateTimeFormat.format(date)
    }

    /** 格式化日期为展示格式：MM月dd日 */
    fun formatDateDisplay(date: Date): String {
        return dateDisplayFormat.format(date)
    }

    /** 获取星期几的中文名称 */
    fun getWeekDayName(date: Date): String {
        return weekDayFormat.format(date)
    }

    /** 解析 yyyy-MM-dd 格式的日期字符串 */
    fun parseDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    /** 解析 yyyy-MM-dd HH:mm:ss 格式的日期时间字符串 */
    fun parseDateTime(dateTimeStr: String): Date? {
        return try {
            dateTimeFormat.parse(dateTimeStr)
        } catch (e: Exception) {
            null
        }
    }

    /** 获取今天的日期字符串（yyyy-MM-dd） */
    fun getToday(): String {
        return formatDate(Date())
    }

    /** 获取明天的日期字符串 */
    fun getTomorrow(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return formatDate(calendar.time)
    }

    /** 获取指定天数后的日期字符串 */
    fun getDateAfterDays(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return formatDate(calendar.time)
    }

    /** 获取指定天数前的日期字符串 */
    fun getDateBeforeDays(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -days)
        return formatDate(calendar.time)
    }

    /** 获取预售期天数（30天） */
    fun getPreSalePeriod(): Int {
        return 30
    }

    /** 获取预售期最后一天的日期字符串 */
    fun getMaxBookingDate(): String {
        return getDateAfterDays(getPreSalePeriod())
    }

    /** 判断给定日期字符串是否在有效的购票日期范围内（今天到预售期内） */
    fun isValidBookingDate(dateStr: String): Boolean {
        val date = parseDate(dateStr) ?: return false
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val maxDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, getPreSalePeriod())
        }.time

        return !date.before(today) && !date.after(maxDate)
    }

    /** 计算两个日期字符串之间的天数差 */
    fun getDaysBetween(startDate: String, endDate: String): Int {
        val start = parseDate(startDate) ?: return 0
        val end = parseDate(endDate) ?: return 0

        val diff = end.time - start.time
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    /** 获取指定日期偏移天数的日期字符串 */
    fun getOffsetDate(baseDate: String, offsetDays: Int): String {
        val date = parseDate(baseDate) ?: return getToday()
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, offsetDays)
        return formatDate(calendar.time)
    }

    /** 判断给定日期是否为今天 */
    fun isToday(dateStr: String): Boolean {
        return dateStr == getToday()
    }

    /** 判断给定日期是否为明天 */
    fun isTomorrow(dateStr: String): Boolean {
        return dateStr == getTomorrow()
    }

    /** 获取日期的展示文本（今天/明天/后天/日期+星期） */
    fun getDateDisplayText(dateStr: String): String {
        val today = getToday()
        val tomorrow = getTomorrow()
        val dayAfterTomorrow = getDateAfterDays(2)
        val date = parseDate(dateStr) ?: return dateStr

        return when (dateStr) {
            today -> "今天"
            tomorrow -> "明天"
            dayAfterTomorrow -> "后天"
            else -> "${formatDateDisplay(date)} ${getWeekDayName(date)}"
        }
    }

    /** 获取今天0点的时间戳（用于MaterialDatePicker的最小日期限制） */
    fun getTodayStartMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /** 获取预售期结束的时间戳（用于MaterialDatePicker的最大日期限制） */
    fun getMaxBookingDateMillis(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, getPreSalePeriod())
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    /** 解析时间字符串（HH:mm）为分钟数，用于时间段筛选比较 */
    fun parseTimeToMinutes(timeStr: String): Int {
        val parts = timeStr.split(":")
        if (parts.size != 2) return 0
        val hours = parts[0].toIntOrNull() ?: 0
        val minutes = parts[1].toIntOrNull() ?: 0
        return hours * 60 + minutes
    }

    /** 解析历时字符串（如 "05:30" 或 "5小时30分"）为分钟数，用于排序比较 */
    fun parseDurationToMinutes(duration: String): Int {
        if (duration.contains(":")) {
            val parts = duration.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                return hours * 60 + minutes
            }
        }
        var totalMinutes = 0
        val hourRegex = "(\\d+)小时".toRegex()
        val minuteRegex = "(\\d+)分".toRegex()
        hourRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull()?.let {
            totalMinutes += it * 60
        }
        minuteRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull()?.let {
            totalMinutes += it
        }
        if (totalMinutes == 0) {
            duration.filter { it.isDigit() }.toIntOrNull()?.let {
                totalMinutes = it
            }
        }
        return totalMinutes
    }
}
