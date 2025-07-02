package com.dx.mobile.captcha.demo.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoginRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "country_code") val countryCode: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String,
    @ColumnInfo(name = "login_time") val loginTime: Long,
    @ColumnInfo(name = "sms_code") val smsCode: String,
    val token: String,
)