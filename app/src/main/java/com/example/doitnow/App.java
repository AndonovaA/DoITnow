package com.example.doitnow;

import android.app.Application;
import android.content.Context;

import com.example.doitnow.db.AppDatabase;

import java.util.UUID;


public class App extends Application {

    public static Context mContext;
    public static String APP_CODE = UUID.randomUUID().toString();
    public static int APP_UNIQUE_NUMBER = 221298;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        AppDatabase.destroyDB();
    }
}
