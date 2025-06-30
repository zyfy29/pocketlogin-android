package com.dx.mobile.captcha.demo.schema

data class SendSmsRequest(
    val mobile: String,
    val area: String,
    val intelligenceToken: String,
    val businessCode: Int = 1,
    val deviceToken: String
)