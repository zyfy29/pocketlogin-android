package com.dx.mobile.captcha.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

import com.king.zxing.CameraScan;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author white
 * @description：
 * @date 2019/6/26
 */
public class CaptchaConfigActivity extends Activity implements View.OnClickListener {
    public static final String KEY_TITLE = "key_title";
    public static final int REQUEST_CODE_SCAN = 0X01;
    public static final int RESULT_OK = 0X01;
    private static final String TAG = "ConfigAC";
    private LinearLayout layout_scanBtn;

    LinearLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha_config);
        layout_scanBtn = findViewById(R.id.layout_configBtn);
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                200);

        for (HashMap<String, Object> profile : Profiles.profiles) {
            Button btn = new Button(this);
            String profileName = (String) profile.get("profileName");
            btn.setText(profileName);
            btn.setTag(profileName);
            btn.setOnClickListener(this);
            layout_scanBtn.addView(btn, layoutParams);
        }
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        String profileName = (String) view.getTag();
        HashMap<String, Object> profile = Profiles.find(profileName);
        if (profile != null) {
            Profiles.defaultProfile = profile;
            Toast.makeText(CaptchaConfigActivity.this, "switch to " + profileName, Toast.LENGTH_SHORT).show();
            CaptchaConfigActivity.this.finish();
        } else {
            Toast.makeText(CaptchaConfigActivity.this, "profile " + profileName + " not found", Toast.LENGTH_LONG).show();
        }
    }

    private void startScan(Class<?> cls, String title) {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.in, R.anim.out);
        Intent intent = new Intent(this, cls);
        intent.putExtra(KEY_TITLE, title);
        ActivityCompat.startActivityForResult(this, intent, REQUEST_CODE_SCAN, optionsCompat.toBundle());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN:
                    String result = CameraScan.parseScanResult(data);
                    enqueueQRCodeConfig(result);
                    break;
            }
        }
    }

    OkHttpClient okHttpClient = new OkHttpClient();

    private void enqueueQRCodeConfig(String qrcodeResult) {
        Request request = new Request.Builder()
                .url(qrcodeResult)
                .get().build();
        final Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "OKHttp3 onFailure", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (ResponseBody body = response.body()) {
                        String profileName = Profiles.add(new JSONObject(body.string()));
                        Profiles.store(CaptchaConfigActivity.this.getApplicationContext());
                        runOnUiThread(() -> {
                            boolean exists = false;
                            for (int i = 0; i < layout_scanBtn.getChildCount(); i++) {
                                View view = layout_scanBtn.getChildAt(i);
                                if (profileName.equals(view.getTag())) {
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                Button btn = new Button(CaptchaConfigActivity.this);
                                btn.setText(profileName);
                                btn.setTag(profileName);
                                btn.setOnClickListener(CaptchaConfigActivity.this);
                                layout_scanBtn.addView(btn, layoutParams);
                            }
                        });
                    } catch (Exception e) {
                        onFailure(call, new IOException(e));
                    }
                }
            }
        });
    }
}
