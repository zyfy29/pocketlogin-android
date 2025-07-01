// app/src/main/java/com/dx/mobile/captcha/demo/ApiRepository.kt
package com.dx.mobile.captcha.demo

import com.dx.mobile.captcha.demo.schema.AppInfoHeader
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.logging.HttpLoggingInterceptor
import java.util.UUID

object ApiRepository {
    private const val BASE_URL = "https://pocketapi.48.cn/"
    private val deviceId: String = UUID.randomUUID().toString()


    private class HeaderInterceptor(
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val appInfo = AppInfoHeader(deviceId = deviceId)
            val appInfoJson = Gson().toJson(appInfo)
            val request = chain.request().newBuilder()
                .addHeader("appInfo", appInfoJson)
                .build()
            return chain.proceed(request)
        }
    }

    // ログ用インターセプターを追加
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // 必要に応じて変更
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .addInterceptor(loggingInterceptor) // ここを追加
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}