package com.dx.mobile.captcha.demo.repo

import com.dx.mobile.captcha.demo.schema.AppLoginBody
import com.dx.mobile.captcha.demo.schema.AppLoginRequest
import com.dx.mobile.captcha.demo.schema.PocketResponse
import com.dx.mobile.captcha.demo.schema.SendSmsBody
import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import retrofit2.Response

class MockLoginRepository : LoginRepository {
    var sendSmsResponse: Response<PocketResponse<SendSmsBody>>? = null
    var appLoginResponse: Response<PocketResponse<AppLoginBody>>? = null
    var sendSmsCallCount = 0
    var appLoginCallCount = 0

    override suspend fun sendSms(request: SendSmsRequest): Response<PocketResponse<SendSmsBody>> {
        sendSmsCallCount++
        return sendSmsResponse ?: Response.success(
            PocketResponse(
                200, true, "success", SendSmsBody("1")
            )
        )
    }

    override suspend fun appLogin(request: AppLoginRequest): Response<PocketResponse<AppLoginBody>> {
        appLoginCallCount++
        return appLoginResponse ?: Response.success(
            PocketResponse(
                200, true, "success",
                AppLoginBody(token = "test_token", userInfo = null, type = 1)
            )
        )
    }

    fun reset() {
        sendSmsResponse = null
        appLoginResponse = null
        sendSmsCallCount = 0
        appLoginCallCount = 0
    }
}