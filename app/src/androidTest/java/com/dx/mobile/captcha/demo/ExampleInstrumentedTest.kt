// app/src/androidTest/java/com/dx/mobile/captcha/demo/ExampleInstrumentedTest.kt
package com.dx.mobile.captcha.demo

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Hello World インストルメンテーションテスト
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // アプリのコンテキストを取得
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // パッケージ名の検証（build.gradleのapplicationIdと一致すること）
        assertEquals("com.dx.mobile.captcha.demo_poc", appContext.packageName)
    }
}