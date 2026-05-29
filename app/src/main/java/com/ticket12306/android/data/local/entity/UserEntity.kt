package com.ticket12306.android.data.local.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String = "",
    val realName: String = "",
    val idType: String = "",
    val idNo: String = "",
    val phone: String = "",
    val email: String = "",
    val token: String = "",
    val uamtk: String = "",
    val newapptk: String = "",
    val isLoggedIn: Boolean = false,
    val lastLoginTime: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable
