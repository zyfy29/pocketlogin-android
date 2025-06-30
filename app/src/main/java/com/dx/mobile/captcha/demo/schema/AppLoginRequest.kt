package com.dx.mobile.captcha.demo.schema

data class AppLoginRequest(
    val mobileCodeLogin: MobileCodeLogin,
    val intelligenceToken: String,
    val loginType: String = "MOBILE_SMS_CODE",
    val deviceToken: String
)

data class MobileCodeLogin(
    val area: String,
    val mobile: String,
    val code: String
)

