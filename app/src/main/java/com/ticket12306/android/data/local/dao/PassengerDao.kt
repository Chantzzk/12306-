package com.ticket12306.android.data.local.dao

import androidx.room.*
import com.ticket12306.android.data.model.Passenger
import kotlinx.coroutines.flow.Flow

@Dao
interface PassengerDao {

    @Query("SELECT * FROM passengers ORDER BY passenger_name ASC")
    fun getAllPassengers(): Flow<List<Passenger>>

    @Query("SELECT * FROM passengers WHERE code IN (:codes)")
    suspend fun getPassengersByCodes(codes: List<String>): List<Passenger>

    @Query("SELECT * FROM passengers WHERE code = :code")
    suspend fun getPassengerByCode(code: String): Passenger?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassengers(passengers: List<Passenger>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassenger(passenger: Passenger)

    @Update
    suspend fun updatePassenger(passenger: Passenger)

    @Delete
    suspend fun deletePassenger(passenger: Passenger)

    @Query("DELETE FROM passengers")
    suspend fun deleteAllPassengers()

    @Query("SELECT COUNT(*) FROM passengers")
    suspend fun getPassengerCount(): Int
}
