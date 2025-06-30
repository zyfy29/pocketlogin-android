package com.dx.mobile.captcha.demo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.dx.mobile.captcha.DXCaptchaListener

class CaptchaLoginActivity : Activity() {
    private var mCaptchaToken: String? = null
    private var mWay: Int = 0
    private var mVersion: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mWay = intent.getIntExtra(KEY_SHOW_WAY, 1)
        mVersion = intent.getIntExtra(KEY_VERSION, 1)

        if (mVersion == 5) {
            setContentView(R.layout.activity_captcha_login_v5)
        } else {
            setContentView(R.layout.activity_captcha_login)
        }
    }

    fun onClickLogin(v: View) {
        // TODO Implement your login logic here
    }

    fun showDialog() {
        Log.i(TAG, "show dialog v$mVersion")
        val mainHandler = Handler(Looper.getMainLooper())
        val mCaptDialog = CaptchaDialog(this, mVersion)

        var passByServer = false
        mCaptDialog.setListener(object : DXCaptchaListener {
            override fun handleEvent(
                webView: WebView?,
                dxCaptchaEvent: String?,
                map: MutableMap<String, String>?
            ) {
                Log.e(TAG, "dxCaptchaEvent:$dxCaptchaEvent")
                when (dxCaptchaEvent) {
                    "passByServer" -> passByServer = true
                    "success" -> {
                        Log.i(TAG, map.toString()) // {"token":"xxx"}
                        mCaptchaToken = map?.get("token") as String
                        Toast.makeText(this@CaptchaLoginActivity, "验证成功", Toast.LENGTH_SHORT).show()
                        if (passByServer) {
                            mainHandler.postDelayed({ mCaptDialog.dismiss() }, 800)
                        } else {
                            mCaptDialog.dismiss()
                        }
                    }
                    "onCaptchaJsLoaded" -> {}
                    "onCaptchaJsLoadFail" -> {
                        // 这种情况下请检查captchaJs配置，或者您cdn网络，或者与之相关的数字证书
                        Toast.makeText(applicationContext, "检测到验证码加载错误，请点击重试", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })

        mCaptDialog.init(-1)

        if (!mCaptDialog.isShowing) {
            mCaptDialog.show()
        }
    }

    fun onSendVerificationCode(v: View) {
        val countryCode = findViewById<EditText>(R.id.country_code).text.toString()
        val phoneNumber = findViewById<EditText>(R.id.phone_number).text.toString()

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show()
            return
        }

        if (mCaptchaToken.isNullOrEmpty()) {
            showDialog()
            return
        }

        // TODO Implement your verification code sending logic here
        val sendButton = findViewById<Button>(R.id.send_code_button)
        sendButton.isEnabled = false
        startCountdown(sendButton)
    }

    private fun startCountdown(button: Button, seconds: Int = 60) {
        val handler = Handler(Looper.getMainLooper())
        var remainingSeconds = seconds

        val runnable = object : Runnable {
            override fun run() {
                if (remainingSeconds > 0) {
                    button.text = "${remainingSeconds}s"
                    remainingSeconds--
                    handler.postDelayed(this, 1000)
                } else {
                    button.text = "发送"
                    button.isEnabled = true
                }
            }
        }

        handler.post(runnable)
    }

    companion object {
        private const val TAG = "DXCaptcha"

        const val KEY_SHOW_WAY = "KEY_SHOW_WAY"
        const val KEY_VERSION = "KEY_KEY_VERSION"

        const val WAY_DIALOG = 1
        const val WAY_INLINE = 2
        const val WAY_TOUCH = 3

        const val CAPTCHA_TOKEN_WAIT_INTERVAL = 500L
    }
}