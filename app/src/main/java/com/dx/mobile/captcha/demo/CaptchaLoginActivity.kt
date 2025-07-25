package com.dx.mobile.captcha.demo

import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dx.mobile.captcha.demo.repo.ApiLoginRepository

class CaptchaLoginActivity : AppCompatActivity() {
    private var mWay: Int = 0
    private var mVersion: Int = 0
    private lateinit var viewModel: CaptchaLoginViewModel

    private lateinit var countryCodeEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var verificationCodeEditText: EditText
    private lateinit var sendCodeButton: Button
    private lateinit var tokenDisplayTextView: TextView
    private lateinit var mCaptDialog: CaptchaDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("CaptchaLoginActivity", "onCreate called")
        super.onCreate(savedInstanceState)

        mWay = intent.getIntExtra(KEY_SHOW_WAY, 1)
        mVersion = intent.getIntExtra(KEY_VERSION, 1)

        if (mVersion == 5) {
            setContentView(R.layout.activity_captcha_login_v5)
        } else {
            setContentView(R.layout.activity_captcha_login)
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(CaptchaLoginViewModel::class.java)
        viewModel.loginRepository = ApiLoginRepository(this)

        // Initialize UI elements
        initializeViews()

        // Set up observers
        setupObservers()

        // allow network operations on the main thread for demo purposes
        if (Build.VERSION.SDK_INT > 9) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
    }

    private fun initializeViews() {
        countryCodeEditText = findViewById(R.id.country_code)
        phoneNumberEditText = findViewById(R.id.phone_number)
        verificationCodeEditText = findViewById(R.id.verification_code)
        sendCodeButton = findViewById(R.id.send_code_button)
        tokenDisplayTextView = findViewById(R.id.token_display)
    }

    private fun setupObservers() {
        // Observe login result
        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is CaptchaLoginViewModel.LoginResult.Success -> {
                    Toast.makeText(this, "ログイン成功", Toast.LENGTH_SHORT).show()
                    tokenDisplayTextView.text = result.token
                }

                is CaptchaLoginViewModel.LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Observe send code result
        viewModel.sendCodeResult.observe(this) { result ->
            when (result) {
                is CaptchaLoginViewModel.SendCodeResult.Success -> {
                    Toast.makeText(this, "验证码已发送", Toast.LENGTH_SHORT).show()
                    sendCodeButton.isEnabled = false
                }

                is CaptchaLoginViewModel.SendCodeResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                    sendCodeButton.isEnabled = true
                }
            }
        }

        // Observe captcha state
        viewModel.captchaState.observe(this) { state ->
            when (state) {
                is CaptchaLoginViewModel.CaptchaState.ShowDialog -> {
                    showDialog()
                }

                is CaptchaLoginViewModel.CaptchaState.Success -> {
                    Toast.makeText(this, "验证成功", Toast.LENGTH_SHORT).show()
                    if (state.passByServer) {
                        android.os.Handler().postDelayed({ mCaptDialog.dismiss() }, 800)
                    } else {
                        mCaptDialog.dismiss()
                    }

                    // server may return empty deviceToken at the first time
                    val (_, deviceToken) = viewModel.splitToken()
                    if (deviceToken.isEmpty()) {
                        viewModel.retryCaptcha()
                    } else {
                        onSendVerificationCode(sendCodeButton)
                    }
                }

                is CaptchaLoginViewModel.CaptchaState.Error -> {
                    Toast.makeText(applicationContext, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        // Observe countdown state
        viewModel.countdownState.observe(this) { state ->
            when (state) {
                is CaptchaLoginViewModel.CountdownState.Counting -> {
                    sendCodeButton.text = "${state.seconds}s"
                    sendCodeButton.isEnabled = false
                }

                is CaptchaLoginViewModel.CountdownState.Finished -> {
                    sendCodeButton.text = "发送"
                    sendCodeButton.isEnabled = true
                }
            }
        }
    }

    fun onClickLogin(v: View) {
        val countryCode = countryCodeEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val code = verificationCodeEditText.text.toString()

        viewModel.login(countryCode, phoneNumber, code)
    }

    private fun showDialog() {
        Log.i(TAG, "show dialog v$mVersion")
        mCaptDialog = CaptchaDialog(this, mVersion)
        mCaptDialog.setListener(viewModel.getCaptchaListener())
        mCaptDialog.init(-1)

        if (!mCaptDialog.isShowing) {
            mCaptDialog.show()
        }
    }


    fun onSendVerificationCode(v: View) {
        val countryCode = countryCodeEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()

        viewModel.sendVerificationCode(countryCode, phoneNumber)
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