package com.dx.mobile.captcha.demo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * @author white
 * @description：
 * @date 2019/5/7
 */
public class H5Activity extends Activity {

    WebView mWebview;
    EditText mEtUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h5);

        mWebview = findViewById(R.id.wb_h5);
        mEtUrl = findViewById(R.id.et_url);

        initWebview(mWebview);
    }


    public void onClickUrl(View v){
        String url = mEtUrl.getText().toString();

        mWebview.loadUrl(url);
    }



    private void initWebview(WebView webView){

        if (webView == null) {
            return;
        }

        WebSettings settings = webView.getSettings();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // 测试环境使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebContentsDebuggingEnabled(true);
        }

        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false);

        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
            webView.setOverScrollMode(OVER_SCROLL_NEVER);

        // 移除接口
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.removeJavascriptInterface("searchBoxJavaBridge_");
            webView.removeJavascriptInterface("accessibility");
            webView.removeJavascriptInterface("accessibilityTraversal");
        }

        // 防止越权访问，跨域等安全问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
            settings.setAllowFileAccess(false);

        // 防范漏洞 CNTA-2018-0005
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            settings.setDomStorageEnabled(true);
        }
        webView.setWebViewClient(null);
        webView.resumeTimers();
    }
}
