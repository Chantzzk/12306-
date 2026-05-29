package com.ticket12306.android.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserUamtkResponse(
    val apptk: String? = null,
    val tk: String? = null
) : Parcelable

@Parcelize
data class UserCheckResponse(
    val username: String,
    val name: String? = null,
    val id_no: String? = null,
    val user_type: String? = null
) : Parcelable

@Parcelize
data class UserInfoResponse(
    val name: String,
    val id_type_code: String,
    val id_type_name: String,
    val id_no: String,
    val sex_code: String,
    val sex_name: String,
    val born_date: String,
    val mobile_no: String,
    val email: String?,
    val address: String?
) : Parcelable

@Parcelize
data class EncryptRequest(
    val data: String,
    val key: String
) : Parcelable

@Parcelize
data class EncryptResponse(
    val data: String
) : Parcelable
