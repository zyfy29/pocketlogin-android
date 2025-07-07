package com.dx.mobile.captcha.demo.repo

import com.dx.mobile.captcha.demo.schema.AppLoginBody
import com.dx.mobile.captcha.demo.schema.AppLoginRequest
import com.dx.mobile.captcha.demo.schema.PocketResponse
import com.dx.mobile.captcha.demo.schema.SendSmsBody
import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import retrofit2.Response

interface LoginRepository {
    suspend fun sendSms(request: SendSmsRequest): Response<PocketResponse<SendSmsBody>>
    suspend fun appLogin(request: AppLoginRequest): Response<PocketResponse<AppLoginBody>>
}