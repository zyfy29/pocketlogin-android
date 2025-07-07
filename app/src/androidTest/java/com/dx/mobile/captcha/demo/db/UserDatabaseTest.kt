// app/src/androidTest/java/com/dx/mobile/captcha/demo/db/UserDatabaseTest.kt
package com.dx.mobile.captcha.demo.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class UserDatabaseTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() {
        val user = User(1, "John", "Doe")
        userDao.insertAll(user)

        val users = userDao.getAll()
        assertEquals(1, users.size)
        assertEquals("John", users[0].firstName)
        assertEquals("Doe", users[0].lastName)
    }

    @Test
    @Throws(Exception::class)
    fun findUserByName() {
        val user = User(1, "John", "Doe")
        userDao.insertAll(user)

        val foundUser = userDao.findByName("John", "Doe")
        assertEquals(1, foundUser.uid)
        assertEquals("John", foundUser.firstName)
        assertEquals("Doe", foundUser.lastName)
    }
}