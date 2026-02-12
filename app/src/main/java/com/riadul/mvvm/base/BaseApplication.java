package com.riadul.mvvm.base;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import io.paperdb.Paper;


public class BaseApplication extends MultiDexApplication {
    private static BaseApplication mInstance = null;
    public AppStorage appStorage;
    private static Context mAppContext;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        appStorage = new AppStorage();
        this.setAppContext(getApplicationContext());
        Paper.init(mAppContext);
    }

    public static synchronized BaseApplication getInstance(){
        return mInstance;
    }
    public static Context getAppContext() {
        return mAppContext;
    }
    public void setAppContext(Context mAppContext) {
        BaseApplication.mAppContext = mAppContext;
    }
}
