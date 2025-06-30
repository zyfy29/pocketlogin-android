package com.dx.mobile.captcha.demo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import com.dx.mobile.captcha.DXCaptchaEvent
import com.dx.mobile.captcha.DXCaptchaListener
import com.dx.mobile.captcha.DXCaptchaView

class CaptchaLoginActivity : Activity() {

    private lateinit var mInlineCaptchaView: DXCaptchaView
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

        mInlineCaptchaView = findViewById(R.id.dxVCodeView)
        mInlineCaptchaView.visibility = View.GONE

        if (mWay == WAY_INLINE) {
            showInline()
        }
    }

    override fun onDestroy() {
        mInlineCaptchaView.destroy()
        super.onDestroy()
    }

    fun onClickLogin(v: View) {
        if (!mCaptchaToken.isNullOrEmpty()) {
            Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show()
            return
        }

        if (mWay == WAY_DIALOG) {
            showDialog()
        } else {
            Toast.makeText(this, "验证成功后才可登陆", Toast.LENGTH_SHORT).show()
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

    // deprecated
    fun showInline() {
        mInlineCaptchaView.visibility = View.VISIBLE

        Profiles.initDefaultProfileInto(mInlineCaptchaView)

        mInlineCaptchaView.startToLoad(object : DXCaptchaListener {
            override fun handleEvent(
                webView: WebView?,
                dxCaptchaEvent: String?,
                p2: MutableMap<String, String>?
            ) {
                when (dxCaptchaEvent) {
                    DXCaptchaEvent.DXCAPTCHA_SUCCESS.toString() -> {
                        Toast.makeText(this@CaptchaLoginActivity, "验证成功", Toast.LENGTH_SHORT).show()
                        Log.i("DXCaptcha", "event after dragend")
                    }
                    else -> {}
                }
            }
        })
    }

    companion object {
        const val KEY_SHOW_WAY = "KEY_SHOW_WAY"
        const val KEY_VERSION = "KEY_KEY_VERSION"

        const val WAY_DIALOG = 1
        const val WAY_INLINE = 2
        const val WAY_TOUCH = 3

        private const val TAG = "DXCaptcha"
    }
}