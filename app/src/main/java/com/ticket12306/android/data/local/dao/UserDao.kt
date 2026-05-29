package com.ticket12306.android.data.local.dao

import androidx.room.*
import com.ticket12306.android.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getLoggedInUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY lastLoginTime DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1")
    suspend fun getLoggedInUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1, token = :token, uamtk = :uamtk, newapptk = :newapptk, lastLoginTime = :loginTime WHERE id = :id")
    suspend fun loginUser(id: Long, token: String, uamtk: String, newapptk: String, loginTime: Long = System.currentTimeMillis())

    @Query("UPDATE users SET isLoggedIn = 0 WHERE id = :id")
    suspend fun logoutUser(id: Long)

    @Query("UPDATE users SET token = :token WHERE id = :id")
    suspend fun updateToken(id: Long, token: String)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Transaction
    suspend fun loginAndLogoutOthers(user: UserEntity, token: String, uamtk: String, newapptk: String) {
        logoutAllUsers()
        val updatedUser = user.copy(
            isLoggedIn = true,
            token = token,
            uamtk = uamtk,
            newapptk = newapptk,
            lastLoginTime = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        updateUser(updatedUser)
    }
}
