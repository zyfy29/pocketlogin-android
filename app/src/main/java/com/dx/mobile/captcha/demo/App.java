package com.dx.mobile.captcha.demo;

import android.app.Application;

import com.security.mobile.util.tls12patch.Tls12Patch;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Tls12Patch.tls12Patch(getFilesDir());

        Profiles.load(this);

    }
}
