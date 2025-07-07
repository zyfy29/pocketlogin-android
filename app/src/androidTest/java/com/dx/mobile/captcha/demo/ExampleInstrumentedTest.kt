// app/src/androidTest/java/com/dx/mobile/captcha/demo/ExampleInstrumentedTest.kt
package com.dx.mobile.captcha.demo

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Hello World インストルメンテーションテスト
 */
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        val packageName = if (BuildConfig.DEBUG) {
            "com.dx.mobile.captcha.demo_poc.debug"
        } else {
            "com.dx.mobile.captcha.demo_poc"
        }
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals(packageName, appContext.packageName)
    }
}