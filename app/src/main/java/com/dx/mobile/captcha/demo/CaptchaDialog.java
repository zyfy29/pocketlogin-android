package com.dx.mobile.captcha.demo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dx.mobile.captcha.DXCaptchaListener;
import com.dx.mobile.captcha.DXCaptchaView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blue on 2018/5/3.
 */

public class CaptchaDialog extends Dialog {


    private static final String TAG = "DXCaptcha";

    DXCaptchaView dxCaptcha;

    // percentage of screen width
    private int mPerWidth = 80;

    DXCaptchaListener mListener;

    int mVersion = 0;

    public CaptchaDialog(@NonNull Context context, int version) {
        super(context, android.R.style.Theme_Holo_Dialog_NoActionBar);
        mVersion = version;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mVersion == 5) {
            setContentView(R.layout.dialog_captcha_v5);
        } else {
            setContentView(R.layout.dialog_captcha);
        }
        dxCaptcha = findViewById(R.id.cv_captcha);

        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Point pt = new Point();
        manager.getDefaultDisplay().getSize(pt);

        if (mPerWidth != -1) {
            dxCaptcha.getLayoutParams().width = (int) (1.0 * mPerWidth / 100 * pt.x);
        }

        Log.e(TAG, "CaptchaDialog width:" + dxCaptcha.getLayoutParams().width);

        initView();

    }

    public void init(int perWidth) {
        mPerWidth = perWidth;
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        Profiles.initDefaultProfileInto(dxCaptcha);

        dxCaptcha.setWebViewClient(new WebViewClient());

        // 测试环境使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView webview = new WebView(getContext());
            webview.pauseTimers();
            webview.destroy();
            webview.setWebContentsDebuggingEnabled(true);
        }

        if (mListener != null) {
            dxCaptcha.startToLoad(mListener);
        } else {
            dxCaptcha.startToLoad(new DXCaptchaListener() {
                @Override
                public void handleEvent(WebView view, String event, Map args) {
                    Log.e(TAG, "DXCaptchaEvent :" + event);
                    switch (event) {
                        case "success": {
                            String token = (String) args.get("token");
                            Log.e(TAG, "token :" + token);
                            break;
                        }
                        case "onCaptchaJsLoaded":
                            break;
                        case "onCaptchaJsLoadFail": {
                            // 这种情况下请检查captchaJs配置，或者您cdn网络，或者与之相关的数字证书
                            Toast.makeText(getOwnerActivity().getApplicationContext(), "检测到验证码加载错误，请点击重试", Toast.LENGTH_LONG).show();
                            break;
                        }
                    }
                }
            });
        }

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onDetachedFromWindow() {
        Log.e(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        dxCaptcha.destroy();
    }


    public void setListener(DXCaptchaListener listener) {
        mListener = listener;
    }
}
