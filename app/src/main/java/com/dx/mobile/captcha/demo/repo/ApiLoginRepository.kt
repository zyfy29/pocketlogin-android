package com.dx.mobile.captcha.demo.repo

import com.dx.mobile.captcha.demo.ApiService
import com.dx.mobile.captcha.demo.schema.AppInfoHeader
import com.dx.mobile.captcha.demo.schema.AppLoginBody
import com.dx.mobile.captcha.demo.schema.AppLoginRequest
import com.dx.mobile.captcha.demo.schema.PocketResponse
import com.dx.mobile.captcha.demo.schema.SendSmsBody
import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID

object ApiLoginRepository : LoginRepository {
    private const val BASE_URL = "https://pocketapi.48.cn/"
    private val deviceId: String = UUID.randomUUID().toString()

    private class HeaderInterceptor(
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val appInfo = AppInfoHeader(deviceId = deviceId)
            val appInfoJson = Gson().toJson(appInfo)
            val request = chain.request().newBuilder()
                .addHeader("appInfo", appInfoJson)
                .build()
            return chain.proceed(request)
        }
    }

    private var apiService: ApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 必要に応じて変更
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .addInterceptor(loggingInterceptor) // ここを追加
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    override suspend fun sendSms(request: SendSmsRequest): Response<PocketResponse<SendSmsBody>> {
        return apiService.sendSms(request).execute()
    }

    override suspend fun appLogin(request: AppLoginRequest): Response<PocketResponse<AppLoginBody>> {
        return apiService.appLogin(request).execute()
    }
}