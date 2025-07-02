package com.dx.mobile.captcha.demo

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dx.mobile.captcha.DXCaptchaListener
import com.dx.mobile.captcha.demo.db.LoginRecord
import com.dx.mobile.captcha.demo.schema.AppLoginBody
import com.dx.mobile.captcha.demo.schema.AppLoginRequest
import com.dx.mobile.captcha.demo.schema.MobileCodeLogin
import com.dx.mobile.captcha.demo.schema.PocketResponse
import com.dx.mobile.captcha.demo.schema.SendSmsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class CaptchaLoginViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "DXCaptcha"

    // LiveData to observe in Activity
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _sendCodeResult = MutableLiveData<SendCodeResult>()
    val sendCodeResult: LiveData<SendCodeResult> = _sendCodeResult

    private val _captchaState = MutableLiveData<CaptchaState>()
    val captchaState: LiveData<CaptchaState> = _captchaState

    private val _countdownState = MutableLiveData<CountdownState>()
    val countdownState: LiveData<CountdownState> = _countdownState

    // State variables
    private var captchaToken: String? = null

    fun splitToken(): Pair<String, String> {
        val intelligenceToken = captchaToken ?: ""
        val deviceToken = captchaToken?.substringAfter(":") ?: ""
        return Pair(intelligenceToken, deviceToken)
    }

    // Method to handle login action
    fun login(countryCode: String, phoneNumber: String, code: String) {
        if (phoneNumber.isEmpty() || code.isEmpty()) {
            _loginResult.value = LoginResult.Error("必要な情報を入力してください")
            return
        }

        val (intelligenceToken, deviceToken) = splitToken()
        if (code != "123456" && deviceToken.isEmpty()) {
            _loginResult.value = LoginResult.Error("请先完成验证码验证")
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

        viewModelScope.launch {
            try {
                // mock for debug mode
                val mockContent =
                    AppLoginBody(token = "mock_user_token_12345", userInfo = null, type = 1)
                val mockResponseBody = PocketResponse(
                    success = true,
                    status = 200,
                    message = "成功",
                    content = mockContent
                )
                val mockResponse = Response.success(mockResponseBody)

                val response = if (code == "123456") {
                    mockResponse
                } else {
                    withContext(Dispatchers.IO) {
                        ApiRepository.apiService.appLogin(loginRequest).execute()
                    }
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val userToken = response.body()?.content?.token ?: ""
                    Log.i(tag, "Login successful for user $phoneNumber, token: $userToken")
                    withContext(Dispatchers.IO) {
                        val loginRecord = LoginRecord(
                            countryCode = countryCode,
                            phoneNumber = phoneNumber,
                            loginTime = System.currentTimeMillis(),
                            smsCode = code,
                            token = userToken
                        )
                        App.getDatabase().loginDao().insert(loginRecord)
                    }
                    captchaToken = null // Clear captcha token after successful login
                    _loginResult.value = LoginResult.Success(userToken)
                } else {
                    _loginResult.value = LoginResult.Error(
                        response.body()?.message ?: "ログイン失敗"
                    )
                }
            } catch (e: Exception) {
                Log.e(tag, "Login error: ${e.localizedMessage}", e)
                _loginResult.value = LoginResult.Error("通信エラー: ${e.localizedMessage}")
            }
        }
    }

    // Method to handle verification code sending
    fun sendVerificationCode(countryCode: String, phoneNumber: String) {
        if (phoneNumber.isEmpty()) {
            _sendCodeResult.value = SendCodeResult.Error("请输入手机号")
            return
        }

        val (intelligenceToken, deviceToken) = splitToken()
        if (deviceToken.isEmpty()) {
            _captchaState.value = CaptchaState.ShowDialog
            return
        }

        val request = SendSmsRequest(
            mobile = phoneNumber,
            area = countryCode,
            intelligenceToken = intelligenceToken,
            deviceToken = deviceToken
        )

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiRepository.apiService.sendSms(request).execute()
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    _sendCodeResult.value = SendCodeResult.Success
                    startCountdown()
                } else {
                    _sendCodeResult.value = SendCodeResult.Error(
                        response.body()?.message ?: "发送失败"
                    )
                }
            } catch (e: Exception) {
                _sendCodeResult.value = SendCodeResult.Error("通信エラー: $e")
                Log.e(tag, e.toString())
            }
        }
    }

    // Method to handle captcha events
    fun getCaptchaListener(): DXCaptchaListener {
        var passByServer = false
        val mainHandler = Handler(Looper.getMainLooper())

        return object : DXCaptchaListener {
            override fun handleEvent(
                webView: WebView?,
                dxCaptchaEvent: String?,
                map: MutableMap<String, String>?
            ) {
                Log.e(tag, "dxCaptchaEvent:$dxCaptchaEvent")
                when (dxCaptchaEvent) {
                    "passByServer" -> passByServer = true
                    "success" -> {
                        Log.i(tag, map.toString())
                        captchaToken = map?.get("token") as String
                        _captchaState.value = CaptchaState.Success(passByServer)
                    }

                    "onCaptchaJsLoaded" -> {}
                    "onCaptchaJsLoadFail" -> {
                        _captchaState.value = CaptchaState.Error("检测到验证码加载错误，请点击重试")
                    }
                }
            }
        }
    }

    fun retryCaptcha() {
        _captchaState.value = CaptchaState.ShowDialog
    }

    // Method to handle countdown
    private fun startCountdown(seconds: Int = 60) {
        val handler = Handler(Looper.getMainLooper())
        var remainingSeconds = seconds

        val runnable = object : Runnable {
            override fun run() {
                if (remainingSeconds > 0) {
                    _countdownState.value = CountdownState.Counting(remainingSeconds)
                    remainingSeconds--
                    handler.postDelayed(this, 1000)
                } else {
                    _countdownState.value = CountdownState.Finished
//                    captchaToken = null
                }
            }
        }

        handler.post(runnable)
    }

    // Sealed classes for state
    sealed class LoginResult {
        data class Success(val token: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    sealed class SendCodeResult {
        object Success : SendCodeResult()
        data class Error(val message: String) : SendCodeResult()
    }

    sealed class CaptchaState {
        object ShowDialog : CaptchaState()
        data class Success(val passByServer: Boolean) : CaptchaState()
        data class Error(val message: String) : CaptchaState()
    }

    sealed class CountdownState {
        data class Counting(val seconds: Int) : CountdownState()
        object Finished : CountdownState()
    }
}