package com.dx.mobile.captcha.demo

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.dx.mobile.captcha.demo.db.AppDatabase
import com.dx.mobile.captcha.demo.repo.MockLoginRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class CaptchaLoginViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    companion object {
        private const val TAG = "CaptchaLoginViewModelTest"
    }

    private lateinit var app: App // is it needed?
    private lateinit var database: AppDatabase
    private lateinit var viewModel: CaptchaLoginViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        database = App.getDatabase()
        viewModel = CaptchaLoginViewModel(app)
        viewModel.loginRepository = MockLoginRepository()
    }

    @Test
    fun testInitialState() {
        Assert.assertNull(viewModel.captchaToken)
    }

    @Test
    fun testLoginWithEmptyCaptchaToken() {
        viewModel.login("86", "12333333333", "111111")
        val result = viewModel.loginResult.value
        Log.d(TAG, result.toString())
        Assert.assertTrue(result is CaptchaLoginViewModel.LoginResult.Error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testLoginWithValidCaptchaToken() = runTest {
        viewModel.captchaToken = "intelligenceToken:deviceToken"
        viewModel.login("86", "12333333333", "111111")

        // TODO: how to wait for LiveData to update?
        advanceUntilIdle()
        Thread.sleep(1000)

        val result = viewModel.loginResult.value
        Log.d(TAG, result.toString())
        Assert.assertTrue(result is CaptchaLoginViewModel.LoginResult.Success)
    }
}