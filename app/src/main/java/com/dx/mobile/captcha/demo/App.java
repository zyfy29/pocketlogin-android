package com.dx.mobile.captcha.demo;

import android.app.Application;

import androidx.room.Room;

import com.dx.mobile.captcha.demo.db.AppDatabase;
import com.security.mobile.util.tls12patch.Tls12Patch;

public class App extends Application {
    private static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        Tls12Patch.tls12Patch(getFilesDir());
        Profiles.load(this);

        // Initialize Room database
        database = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "captcha-database"
        ).build();
    }

    public static AppDatabase getDatabase() {
        return database;
    }
}