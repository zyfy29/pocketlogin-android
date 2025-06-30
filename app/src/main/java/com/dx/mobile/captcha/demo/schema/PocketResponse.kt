package com.dx.mobile.captcha.demo.schema

data class PocketResponse<T>(
    val status: Int,
    val success: Boolean,
    val message: String,
    val content: T?
)