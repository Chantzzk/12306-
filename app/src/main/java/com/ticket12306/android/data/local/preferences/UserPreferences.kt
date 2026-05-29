package com.ticket12306.android.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val USERNAME = stringPreferencesKey("username")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val LAST_STATION_QUERY = stringPreferencesKey("last_station_query")
        val DEFAULT_PASSENGER_IDS = stringPreferencesKey("default_passenger_ids")
        val STATION_CACHE_TIMESTAMP = longPreferencesKey("station_cache_timestamp")
        val PHONE = stringPreferencesKey("phone")
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val DEFAULT_REFRESH_INTERVAL = stringPreferencesKey("default_refresh_interval")
        val DEFAULT_MAX_RETRY = stringPreferencesKey("default_max_retry")
        val DEFAULT_STRATEGY = stringPreferencesKey("default_strategy")
        val DEFAULT_ACCEPT_WAITLIST = booleanPreferencesKey("default_accept_waitlist")
    }

    val token: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.TOKEN]
    }

    val username: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USERNAME]
    }

    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME]
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.IS_LOGGED_IN] ?: false
    }

    val lastStationQuery: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LAST_STATION_QUERY]
    }

    val defaultPassengerIds: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_PASSENGER_IDS]?.split(",") ?: emptyList()
    }

    /**
     * 获取车站数据缓存时间戳（毫秒），0表示未缓存
     */
    val stationCacheTimestamp: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.STATION_CACHE_TIMESTAMP] ?: 0L
    }

    val phone: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PHONE]
    }

    val notificationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: true
    }

    val defaultRefreshInterval: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_REFRESH_INTERVAL]?.toIntOrNull() ?: 5
    }

    val defaultMaxRetry: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_MAX_RETRY]?.toIntOrNull() ?: 50
    }

    val defaultStrategy: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_STRATEGY] ?: "NORMAL"
    }

    val defaultAcceptWaitlist: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_ACCEPT_WAITLIST] ?: false
    }

    suspend fun saveLoginInfo(token: String, username: String, userName: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
            preferences[PreferencesKeys.USERNAME] = username
            preferences[PreferencesKeys.USER_NAME] = userName
            preferences[PreferencesKeys.IS_LOGGED_IN] = true
        }
    }

    suspend fun updateToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TOKEN] = token
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun saveLastStationQuery(query: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_STATION_QUERY] = query
        }
    }

    suspend fun saveDefaultPassengerIds(ids: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PASSENGER_IDS] = ids.joinToString(",")
        }
    }

    /**
     * 保存车站数据缓存时间戳
     */
    suspend fun saveStationCacheTimestamp(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.STATION_CACHE_TIMESTAMP] = timestamp
        }
    }

    suspend fun clearLoginInfo() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.TOKEN)
            preferences.remove(PreferencesKeys.USERNAME)
            preferences.remove(PreferencesKeys.USER_NAME)
            preferences.remove(PreferencesKeys.PHONE)
            preferences[PreferencesKeys.IS_LOGGED_IN] = false
        }
    }

    suspend fun savePhone(phone: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PHONE] = phone
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun saveDefaultRefreshInterval(interval: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_REFRESH_INTERVAL] = interval.toString()
        }
    }

    suspend fun saveDefaultMaxRetry(maxRetry: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_MAX_RETRY] = maxRetry.toString()
        }
    }

    suspend fun saveDefaultStrategy(strategy: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_STRATEGY] = strategy
        }
    }

    suspend fun saveDefaultAcceptWaitlist(accept: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_ACCEPT_WAITLIST] = accept
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
