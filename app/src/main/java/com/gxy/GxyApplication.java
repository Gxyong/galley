package com.gxy;

import android.app.Application;


public class GxyApplication extends Application {

    private static GxyApplication gxyApplication;
    //单例模式
    public static synchronized GxyApplication getInstance() {
        return gxyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gxyApplication = this;
    }
}
