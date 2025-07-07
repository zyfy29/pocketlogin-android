package com.dx.mobile.captcha.demo.repo

import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test

class ApiLoginRepositoryTest {
    @Test
    fun testSendSms_real() {
        val request = SendSmsRequest(
            area = "86",
            mobile = "12345678901",
            intelligenceToken = "test_intelligence_token",
            deviceToken = "test"
        )
        runTest {
            val result = ApiLoginRepository.sendSms(request)
            Assert.assertTrue(result.isSuccessful)
            Assert.assertFalse(result.body()?.success ?: false)
            Assert.assertEquals(500, result.body()?.status ?: 0)
        }
    }
}