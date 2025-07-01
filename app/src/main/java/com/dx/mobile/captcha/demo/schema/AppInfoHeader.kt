package com.dx.mobile.captcha.demo.schema

data class AppInfoHeader(
    val vendor: String = "apple",
    val deviceId: String,
    val appVersion: String = "7.1.26",
    val appBuild: String = "25052701",
    val osVersion: String = "18.5.0",
    val osType: String = "ios",
    val deviceName: String = "iPhone 13 Pro",
    val os: String = "ios"
)