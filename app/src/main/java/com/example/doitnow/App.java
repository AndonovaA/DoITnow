package com.example.doitnow;

import android.app.Application;
import android.content.Context;
import com.example.doitnow.db.AppDatabase;


public class App extends Application {

    public static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        //inifcijalizacija na bazata
        mContext = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //unistuvanje na bazata
        AppDatabase.destroyDB();
    }
}
