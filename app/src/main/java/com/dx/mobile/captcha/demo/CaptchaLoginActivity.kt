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
import android.widget.TextView
import android.widget.Toast
import com.dx.mobile.captcha.DXCaptchaListener
import com.dx.mobile.captcha.demo.schema.AppLoginRequest
import com.dx.mobile.captcha.demo.schema.MobileCodeLogin
import com.dx.mobile.captcha.demo.schema.SendSmsRequest

class CaptchaLoginActivity : Activity() {
    private var mToken: String? = null
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
        val countryCode = findViewById<EditText>(R.id.country_code).text.toString()
        val phoneNumber = findViewById<EditText>(R.id.phone_number).text.toString()
        val code = findViewById<EditText>(R.id.verification_code).text.toString()

        if (phoneNumber.isEmpty() || code.isEmpty()) {
            Toast.makeText(this, "必要な情報を入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        val intelligenceToken = mToken ?: ""
        val deviceToken = mToken?.substringAfter(":") ?: ""
        if (intelligenceToken.isEmpty() || deviceToken.isEmpty()) {
            Toast.makeText(this, "请先完成验证码验证", Toast.LENGTH_SHORT).show()
            return
        }

        val loginRequest = AppLoginRequest(
            mobileCodeLogin = MobileCodeLogin(
                area = countryCode,
                mobile = phoneNumber,
                code = code
            ),
            intelligenceToken = intelligenceToken,
            deviceToken = deviceToken
        )

        try {
            val response = ApiRepository.apiService.appLogin(loginRequest).execute()
            if (response.isSuccessful && response.body()?.success == true) {
                Toast.makeText(this@CaptchaLoginActivity, "ログイン成功", Toast.LENGTH_SHORT).show()
                findViewById<TextView>(R.id.token_display).text = response.body()?.content?.token ?: "failed to get token"
            } else {
                Toast.makeText(
                    this@CaptchaLoginActivity,
                    response.body()?.message ?: "ログイン失敗",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@CaptchaLoginActivity,
                "通信エラー: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
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
                        Log.i(TAG, map.toString()) // {"token":"<captChaToken>:<deviceToken>"}
                        mToken = map?.get("token") as String
                        Toast.makeText(this@CaptchaLoginActivity, "验证成功", Toast.LENGTH_SHORT)
                            .show()
                        if (passByServer) {
                            mainHandler.postDelayed({ mCaptDialog.dismiss() }, 800)
                        } else {
                            mCaptDialog.dismiss()
                        }
                    }

                    "onCaptchaJsLoaded" -> {}
                    "onCaptchaJsLoadFail" -> {
                        // 这种情况下请检查captchaJs配置，或者您cdn网络，或者与之相关的数字证书
                        Toast.makeText(
                            applicationContext,
                            "检测到验证码加载错误，请点击重试",
                            Toast.LENGTH_LONG
                        ).show()
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

        val intelligenceToken = mToken ?: ""
        val deviceToken = mToken?.substringAfter(":") ?: ""
        if (intelligenceToken.isEmpty() || deviceToken.isEmpty()) {
            showDialog()
            return
        }

        val sendButton = findViewById<Button>(R.id.send_code_button)
        sendButton.isEnabled = false
        val request = SendSmsRequest(
            mobile = phoneNumber,
            area = countryCode,
            intelligenceToken = intelligenceToken,
            deviceToken = deviceToken
        )

        try {
            val response = ApiRepository.apiService.sendSms(request).execute()
            if (response.isSuccessful && response.body()?.success == true) {
                Toast.makeText(this@CaptchaLoginActivity, "验证码已发送", Toast.LENGTH_SHORT).show()
                startCountdown(sendButton)
            } else {
                Toast.makeText(
                    this@CaptchaLoginActivity,
                    response.body()?.message ?: "发送失败",
                    Toast.LENGTH_SHORT
                ).show()
                sendButton.isEnabled = true
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@CaptchaLoginActivity,
                "通信エラー: $e",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(TAG, e.toString())
            sendButton.isEnabled = true
        }
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
                    mToken = null
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
    }
}