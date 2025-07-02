package com.dx.mobile.captcha.demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    public void onClick(View v) {
//        if (TextUtils.isEmpty(ServerConfig.getDefaultProfile().appid)) {
//            Toast.makeText(this, "需进入「服务配置」填写AppID", Toast.LENGTH_SHORT).show();
//            return;
//        }
        switch (v.getId()) {
            case R.id.btn_captcha_v5:
                onClickPopup(v);
                break;
            case R.id.btn_captcha:
                onClickPopup(v);
                break;
            case R.id.btn_captcha_inline_v5:
                onClickInline(v);
                break;
            case R.id.btn_captcha_inline:
                onClickInline(v);
                break;
            case R.id.btn_h5:
                onClickH5(v);
                break;
            case R.id.btn_login_history:
                onClickLoginHistory(v);
                break;
            case R.id.btn_dan:
                Intent intent = new Intent(this, CaptchaDanActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickPopup(View v) {
        Intent intent = new Intent(this, CaptchaLoginActivity.class);
        intent.putExtra(CaptchaLoginActivity.KEY_SHOW_WAY, CaptchaLoginActivity.WAY_DIALOG);
        if (v.getId() == R.id.btn_captcha_v5) {
            intent.putExtra(CaptchaLoginActivity.KEY_VERSION, 5);
        }
        startActivity(intent);
    }

    public void onClickInline(View v) {
        Intent intent = new Intent(this, CaptchaLoginActivity.class);
        intent.putExtra(CaptchaLoginActivity.KEY_SHOW_WAY, CaptchaLoginActivity.WAY_INLINE);
        if (v.getId() == R.id.btn_captcha_inline_v5) {
            intent.putExtra(CaptchaLoginActivity.KEY_VERSION, 5);
        }
        startActivity(intent);
    }

    public void onClickConfig(View v) {
        Intent intent = new Intent(this, CaptchaConfigActivity.class);
        startActivity(intent);
    }

    public void onClickH5(View v) {
        Intent intent = new Intent(this, H5Activity.class);
        startActivity(intent);
    }

    private void onClickLoginHistory(View v) {
        Intent intent = new Intent(this, LoginHistoryActivity.class);
        startActivity(intent);
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
            });

}
