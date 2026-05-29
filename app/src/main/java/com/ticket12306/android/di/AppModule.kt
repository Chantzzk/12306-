package com.ticket12306.android.di

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ticket12306.android.BookingWorker
import com.ticket12306.android.booking.BookingManager
import com.ticket12306.android.booking.BookingStateManager
import com.ticket12306.android.booking.TicketMonitor
import com.ticket12306.android.data.local.database.AppDatabase
import com.ticket12306.android.data.local.dao.BookingLogDao
import com.ticket12306.android.data.local.dao.BookingTaskDao
import com.ticket12306.android.data.local.preferences.UserPreferences
import com.ticket12306.android.data.remote.RetrofitClient
import com.ticket12306.android.data.remote.api.OrderApi
import com.ticket12306.android.data.remote.api.StationApi
import com.ticket12306.android.data.remote.api.TicketApi
import com.ticket12306.android.data.remote.api.UserApi
import com.ticket12306.android.data.repository.StationRepository
import com.ticket12306.android.data.repository.TicketRepository
import com.ticket12306.android.data.repository.UserRepository
import com.ticket12306.android.util.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideBookingTaskDao(database: AppDatabase): BookingTaskDao {
        return database.bookingTaskDao()
    }

    @Provides
    @Singleton
    fun provideBookingLogDao(database: AppDatabase): BookingLogDao {
        return database.bookingLogDao()
    }

    @Provides
    @Singleton
    fun provideStationApi(): StationApi {
        return RetrofitClient.create()
    }

    @Provides
    @Singleton
    fun provideTicketApi(): TicketApi {
        return RetrofitClient.create()
    }

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return RetrofitClient.create()
    }

    @Provides
    @Singleton
    fun provideOrderApi(): OrderApi {
        return RetrofitClient.create()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApi,
        orderApi: OrderApi,
        database: AppDatabase,
        userPreferences: UserPreferences
    ): UserRepository {
        return UserRepository(userApi, orderApi, database, userPreferences)
    }

    @Provides
    @Singleton
    fun provideTicketRepository(
        ticketApi: TicketApi,
        orderApi: OrderApi,
        database: AppDatabase,
        userPreferences: UserPreferences
    ): TicketRepository {
        return TicketRepository(ticketApi, orderApi, database, userPreferences)
    }

    @Provides
    @Singleton
    fun provideStationRepository(
        stationApi: StationApi,
        database: AppDatabase,
        userPreferences: UserPreferences
    ): StationRepository {
        return StationRepository(stationApi, database, userPreferences)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideTicketMonitor(
        ticketRepository: TicketRepository,
        database: AppDatabase,
        notificationHelper: NotificationHelper
    ): TicketMonitor {
        return TicketMonitor(
            ticketRepository,
            database.bookingTaskDao(),
            database.bookingLogDao()
        )
    }

    @Provides
    @Singleton
    fun provideBookingManager(
        ticketRepository: TicketRepository,
        database: AppDatabase,
        notificationHelper: NotificationHelper,
        ticketMonitor: TicketMonitor
    ): BookingManager {
        return BookingManager(
            ticketRepository,
            database.bookingTaskDao(),
            database.bookingLogDao(),
            notificationHelper,
            ticketMonitor
        )
    }

    @Provides
    @Singleton
    fun provideBookingStateManager(
        database: AppDatabase,
        bookingManager: BookingManager
    ): BookingStateManager {
        return BookingStateManager(
            database.bookingTaskDao(),
            database.bookingLogDao(),
            bookingManager
        )
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

}
