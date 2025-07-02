package com.dx.mobile.captcha.demo.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, LoginRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun loginDao(): LoginDao
}