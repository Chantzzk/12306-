package com.ticket12306.android.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.local.dao.PassengerDao
import com.ticket12306.android.data.local.dao.QueryHistoryDao
import com.ticket12306.android.data.local.dao.StationDao
import com.ticket12306.android.data.local.dao.UserDao
import com.ticket12306.android.data.local.entity.QueryHistoryEntity
import com.ticket12306.android.data.local.entity.UserEntity
import com.ticket12306.android.data.model.BookingLog
import com.ticket12306.android.data.model.BookingTask
import com.ticket12306.android.data.model.Passenger
import com.ticket12306.android.data.model.Station

@Database(
    entities = [
        Station::class,
        Passenger::class,
        BookingTask::class,
        UserEntity::class,
        QueryHistoryEntity::class,
        BookingLog::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao
    abstract fun passengerDao(): PassengerDao
    abstract fun bookingTaskDao(): BookingTaskDao
    abstract fun bookingLogDao(): BookingLogDao
    abstract fun userDao(): UserDao
    abstract fun queryHistoryDao(): QueryHistoryDao

    companion object {
        private const val DATABASE_NAME = "ticket_12306.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
