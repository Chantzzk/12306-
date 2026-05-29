package com.ticket12306.android.data.local.dao

import androidx.room.*
import com.ticket12306.android.data.model.Station
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Query("SELECT * FROM stations ORDER BY name ASC")
    fun getAllStations(): Flow<List<Station>>

    /**
     * 综合搜索车站：按名称、拼音、拼音首字母、代码、城市模糊匹配
     * 排序优先级：名称精确匹配 > 名称前缀匹配 > 城市匹配 > 拼音前缀匹配 > 其他
     */
    @Query("""
        SELECT * FROM stations 
        WHERE name LIKE '%' || :query || '%' 
           OR pinyin LIKE '%' || :query || '%' 
           OR pinyinInitial LIKE '%' || :query || '%' 
           OR code LIKE '%' || :query || '%'
           OR city LIKE '%' || :query || '%'
        ORDER BY 
            CASE WHEN name = :query THEN 0 ELSE 1 END,
            CASE WHEN name LIKE :query || '%' THEN 0 ELSE 1 END,
            CASE WHEN city = :query THEN 0 ELSE 1 END,
            CASE WHEN pinyin LIKE :query || '%' THEN 0 ELSE 1 END,
            CASE WHEN pinyinInitial = :query THEN 0 ELSE 1 END,
            name ASC
    """)
    fun searchStations(query: String): Flow<List<Station>>

    /**
     * 按城市名搜索车站，返回该城市下所有车站
     */
    @Query("SELECT * FROM stations WHERE city LIKE '%' || :city || '%' ORDER BY name ASC")
    fun searchByCity(city: String): Flow<List<Station>>

    /**
     * 根据车站代码精确查询
     */
    @Query("SELECT * FROM stations WHERE code = :code")
    suspend fun getStationByCode(code: String): Station?

    /**
     * 根据车站名称精确查询
     */
    @Query("SELECT * FROM stations WHERE name = :name LIMIT 1")
    suspend fun getStationByName(name: String): Station?

    /**
     * 根据多个车站代码批量查询（用于热门车站加载）
     */
    @Query("SELECT * FROM stations WHERE code IN (:codes) ORDER BY name ASC")
    fun getStationsByCodes(codes: List<String>): Flow<List<Station>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<Station>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: Station)

    @Query("DELETE FROM stations")
    suspend fun deleteAllStations()

    @Query("SELECT COUNT(*) FROM stations")
    suspend fun getStationCount(): Int

    /**
     * 事务操作：先删除所有车站再批量插入，确保数据一致性
     */
    @Transaction
    suspend fun replaceAllStations(stations: List<Station>) {
        deleteAllStations()
        insertStations(stations)
    }
}
