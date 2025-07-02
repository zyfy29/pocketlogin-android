package com.dx.mobile.captcha.demo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LoginDao {
    @Query("SELECT * FROM loginrecord ORDER BY id DESC")
    fun getAll(): List<LoginRecord>

    @Insert
    fun insert(record: LoginRecord)

    @Delete
    fun delete(record: LoginRecord)
}