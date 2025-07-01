package com.dx.mobile.captcha.demo

import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ApiRepositoryTest {

    @Test
    fun `apiService created`() {
        val service = ApiRepository.apiService
        assertNotNull(service)
    }

    @Test
    fun `sendSms`() {
        val service = ApiRepository.apiService
        val request = SendSmsRequest(
            area = "86",
            mobile = "12345678901",
            intelligenceToken = "test_intelligence_token",
            deviceToken = "test"
        )
        val call = service.sendSms(request)
        assertNotNull(call)
        var result = call.execute()
        assertNotNull(result)
    }
}