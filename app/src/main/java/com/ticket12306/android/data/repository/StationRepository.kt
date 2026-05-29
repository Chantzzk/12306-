package com.ticket12306.android.data.repository

import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.model.Station
import com.ticket12306.android.data.remote.api.StationApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StationRepository(
    private val stationApi: StationApi,
    private val database: AppDatabase,
    private val userPreferences: UserPreferences
) {

    companion object {
        /** 缓存有效期：7天（毫秒） */
        private const val CACHE_EXPIRY_MS = 7 * 24 * 60 * 60 * 1000L

        /** 热门车站代码列表 */
        val HOT_STATION_CODES = listOf(
            "BJP", "SHH", "GZQ", "SZQ", "CQW", "CDW", "HZH", "NJH", "WHN", "XAY",
            "CSQ", "TJP", "SYT", "DLT", "JNK", "QDK", "FZS", "KMM", "NNZ", "GZQ",
            "HBB", "LZJ", "TYV", "HFN", "SNN", "NKG", "WJQ", "CWH", "YIJ", "XNZ"
        )

        /** 热门车站名称映射（代码 -> 名称），用于本地无缓存时展示 */
        val HOT_STATION_NAMES = mapOf(
            "BJP" to "北京", "SHH" to "上海", "GZQ" to "广州", "SZQ" to "深圳",
            "CQW" to "重庆", "CDW" to "成都", "HZH" to "杭州", "NJH" to "南京",
            "WHN" to "武汉", "XAY" to "西安", "CSQ" to "长沙", "TJP" to "天津",
            "SYT" to "沈阳", "DLT" to "大连", "JNK" to "济南", "QDK" to "青岛",
            "FZS" to "福州", "KMM" to "昆明", "NNZ" to "南宁", "HBB" to "哈尔滨",
            "LZJ" to "兰州", "TYV" to "太原", "HFN" to "合肥", "SNN" to "三亚",
            "NKG" to "南京南"
        )
    }

    private val stationDao = database.stationDao()

    /** 所有车站数据流 */
    val allStations: Flow<List<Station>> = stationDao.getAllStations()

    /**
     * 综合搜索车站：支持按名称、拼音、拼音首字母、城市搜索
     * 搜索结果按优先级排序：精确匹配 > 前缀匹配 > 包含匹配
     */
    fun searchStations(query: String): Flow<List<Station>> {
        return stationDao.searchStations(query)
    }

    /**
     * 按城市名搜索该城市所有车站
     */
    fun searchByCity(city: String): Flow<List<Station>> {
        return stationDao.searchByCity(city)
    }

    /**
     * 根据车站代码精确查询
     */
    suspend fun getStationByCode(code: String): Station? {
        return stationDao.getStationByCode(code)
    }

    /**
     * 根据车站名称精确查询
     */
    suspend fun getStationByName(name: String): Station? {
        return stationDao.getStationByName(name)
    }

    /**
     * 获取热门车站列表
     * 从本地数据库中按代码批量查询，确保数据一致性
     */
    fun getHotStations(): Flow<List<Station>> {
        return stationDao.getStationsByCodes(HOT_STATION_CODES)
    }

    /**
     * 从网络获取车站数据并缓存到本地数据库
     * 成功后更新缓存时间戳
     */
    suspend fun fetchStations(): Result<List<Station>> {
        return try {
            val response = stationApi.getStationNames()

            if (response.isSuccessful) {
                val stationData = response.body() ?: ""
                val stations = parseStationData(stationData)
                stationDao.replaceAllStations(stations)
                userPreferences.saveStationCacheTimestamp(System.currentTimeMillis())
                Result.success(stations)
            } else {
                Result.failure(Exception("获取车站数据失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 解析12306车站数据格式
     * 数据格式：@bjb|北京北|VAP|beijingbei|bjb|0|0@bjn|北京南|VNP|beijingnan|bjn|1|0
     * 各字段含义：拼音简写|车站名|电报码|拼音全拼|拼音简写|省份代码|城市代码
     */
    private fun parseStationData(data: String): List<Station> {
        val stations = mutableListOf<Station>()

        try {
            val stationInfoStr = data.substringAfter("var station_names ='")
                .substringBefore("';")

            val stationList = stationInfoStr.split("@")

            for (stationStr in stationList) {
                if (stationStr.isNotBlank()) {
                    val parts = stationStr.split("|")
                    if (parts.size >= 4) {
                        val station = Station(
                            code = parts[2],
                            name = parts[1],
                            pinyin = parts[3],
                            pinyinInitial = parts[0],
                            province = parts.getOrNull(4),
                            city = parts.getOrNull(5)
                        )
                        stations.add(station)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return stations
    }

    /**
     * 检查本地缓存是否已过期
     * 过期条件：超过7天未更新
     */
    suspend fun isCacheExpired(): Boolean {
        val timestamp = userPreferences.stationCacheTimestamp.first()
        if (timestamp == 0L) return true
        return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS
    }

    /**
     * 检查本地是否有车站数据
     */
    suspend fun hasStations(): Boolean {
        return stationDao.getStationCount() > 0
    }

    /**
     * 获取本地车站数量
     */
    suspend fun getStationCount(): Int {
        return stationDao.getStationCount()
    }
}
