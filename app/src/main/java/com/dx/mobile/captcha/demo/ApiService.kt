package com.dx.mobile.captcha.demo

import com.dx.mobile.captcha.demo.schema.AppLoginBody
import com.dx.mobile.captcha.demo.schema.AppLoginRequest
import com.dx.mobile.captcha.demo.schema.PocketResponse
import com.dx.mobile.captcha.demo.schema.SendSmsBody
import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import retrofit2.Call
import retrofit2.http.POST

import retrofit2.http.Body

interface ApiService {
    @POST("user/api/v2/sms/send_sms")
    fun sendSms(@Body request: SendSmsRequest): Call<PocketResponse<SendSmsBody>>

    @POST("user/api/v2/login/app/app_login")
    fun appLogin(@Body request: AppLoginRequest): Call<PocketResponse<AppLoginBody>>
}