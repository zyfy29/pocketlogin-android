package com.dx.mobile.captcha.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dx.mobile.captcha.DXCaptchaEvent;
import com.dx.mobile.captcha.DXCaptchaListener;
import com.dx.mobile.captcha.DXCaptchaView;

import java.util.Map;
import java.util.Random;

/**
 * @author white
 * @description：
 * @date 2019/6/18
 */
public class CaptchaDanActivity extends Activity {

    private static final String TAG = "DXCaptcha";

    DXCaptchaView mInlineCaptchaView;
    boolean ready = false;

    boolean mSuccess;

    Button btn;
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dan_v5);
        mInlineCaptchaView = findViewById(R.id.dxVCodeView);
        btn = findViewById(R.id.btn);
        msg = findViewById(R.id.msg);
        // 先把验证码藏起来
        mInlineCaptchaView.setVisibility(View.INVISIBLE);

        // 后台加载，完成渲染
        showInline();
    }

    public void onDestroy() {
        mInlineCaptchaView.destroy();
        super.onDestroy();
    }


    void onDan() { // 有单进来要抢了
        // 验证码已经加载完毕，可以展现了
        // FIXME 等待时间太长了，需要刷新一下
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                msg.setText("抢单 ... ");
                if (mInlineCaptchaView.getVisibility() != View.VISIBLE) {
                    // 是时候展现真正的技术了
                    mInlineCaptchaView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    class FetchThread extends Thread {
        @Override
        public void run() {
            // 模拟喂单
            while (true) {
                try {
                    int sec = 3 + new Random().nextInt(3);
                    while (sec >= 0) {
                        Thread.sleep(1000);
                        int finalSec = sec;
                        runOnUiThread(() -> msg.setText(finalSec + " s 后开始抢单"));
                        sec--;
                    }

                    onDan();
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    FetchThread t;

    public void onClickLogin(View v) {
        Button btn = (Button) v;
        if (t == null) {
            t = new FetchThread();
            t.start();
            btn.setText("结束");
        } else {
            t.interrupt();
            t = null;
            btn.setText("开始抢单");
            msg.setText("");
        }
    }

    public void showInline() {
        Profiles.initDefaultProfileInto(mInlineCaptchaView);

        mInlineCaptchaView.startToLoad(new DXCaptchaListener() {
            @Override
            public void handleEvent(WebView webView0, DXCaptchaEvent dxCaptchaEvent, Map map) {
                final DXCaptchaView webview = (DXCaptchaView) webView0;
                switch (dxCaptchaEvent) {
                    case DXCAPTCHA_AFTER_RENDER:
                    case DXCAPTCHA_READY:
                        ready = true; // 加载好了
                        break;
                    case DXCAPTCHA_SUCCESS: // 验证通过了
                        mSuccess = true;
                        Toast.makeText(CaptchaDanActivity.this, "验证成功", Toast.LENGTH_SHORT).show();
                        new Thread(() -> {
                            ready = false;
                            try {
                                Thread.sleep(200); //界面慢点
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            runOnUiThread(() -> {
                                // 准备下一个单
                                webview.setVisibility(View.INVISIBLE);
                                webview.reload();
                            });
                        }).start();
                        break;
                }
            }
        });
    }
}
