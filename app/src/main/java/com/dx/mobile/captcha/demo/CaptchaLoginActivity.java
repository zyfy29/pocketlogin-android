package com.dx.mobile.captcha.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.dx.mobile.captcha.DXCaptchaEvent;
import com.dx.mobile.captcha.DXCaptchaListener;
import com.dx.mobile.captcha.DXCaptchaView;

import java.util.Map;

/**
 * @author white
 * @description：
 * @date 2019/6/18
 */
public class CaptchaLoginActivity extends Activity {

    public static final String KEY_SHOW_WAY = "KEY_SHOW_WAY";
    public static final String KEY_VERSION = "KEY_KEY_VERSION";

    public static final int WAY_DIALOG = 1;
    public static final int WAY_INLINE = 2;
    public static final int WAY_TOUCH = 3;

    private static final String TAG = "DXCaptcha";

    DXCaptchaView mInlineCaptchaView;

    boolean mSuccess;
    int mWay;
    int mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWay = getIntent().getIntExtra(KEY_SHOW_WAY, 1);
        mVersion = getIntent().getIntExtra(KEY_VERSION, 1);
        if (mVersion == 5) {
            setContentView(R.layout.activity_captcha_login_v5);
        } else {
            setContentView(R.layout.activity_captcha_login);
        }
        mInlineCaptchaView = findViewById(R.id.dxVCodeView);

        mInlineCaptchaView.setVisibility(View.GONE);
        if (mWay == WAY_INLINE) {
            showInline();
        }
//        else if(mWay == WAY_DIALOG) {
//            showDialog();
//        }
    }

    public void onDestroy() {
        mInlineCaptchaView.destroy();
        super.onDestroy();
    }


    public void onClickLogin(View v) {
        if (mSuccess) {
            Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mWay == WAY_DIALOG) {
            showDialog();
        } else {
            Toast.makeText(this, "验证成功后才可登陆", Toast.LENGTH_SHORT).show();
        }


    }

    public void showDialog() {

        Log.i(TAG, "show dialog v" + mVersion);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        final CaptchaDialog mCaptDialog = new CaptchaDialog(this, mVersion);
        mCaptDialog.setListener(new DXCaptchaListener() {
            boolean passByServer;
            @Override
            public void handleEvent(WebView webView, String dxCaptchaEvent, Map map) {
                Log.e(TAG, "dxCaptchaEvent:" + dxCaptchaEvent);
                switch (dxCaptchaEvent) {
                    case "passByServer":
                        passByServer = true;
                        break;
                    case "success":
                        mSuccess = true;
                        Toast.makeText(CaptchaLoginActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        if (passByServer) {
                            mainHandler.postDelayed(mCaptDialog::dismiss, 800);
                        } else {
                            mCaptDialog.dismiss();
                        }
                        break;
                    case "onCaptchaJsLoaded":
                        break;
                    case "onCaptchaJsLoadFail": {
                        // 这种情况下请检查captchaJs配置，或者您cdn网络，或者与之相关的数字证书
                        Toast.makeText(getApplicationContext(), "检测到验证码加载错误，请点击重试", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        });

        mCaptDialog.init(-1);

        if (!mCaptDialog.isShowing()) {
            mCaptDialog.show();
        }
    }


    public void showInline() {
        mInlineCaptchaView.setVisibility(View.VISIBLE);

        Profiles.initDefaultProfileInto(mInlineCaptchaView);

        mInlineCaptchaView.startToLoad(new DXCaptchaListener() {
            @Override
            public void handleEvent(WebView webView, DXCaptchaEvent dxCaptchaEvent, Map map) {
                switch (dxCaptchaEvent) {
                    case DXCAPTCHA_SUCCESS:
                        mSuccess = true;
                        Toast.makeText(CaptchaLoginActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        Log.i("DXCaptcha", "event after dragend");
                        break;
                }
            }
        });
    }

}
