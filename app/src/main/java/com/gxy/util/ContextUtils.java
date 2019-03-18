package com.gxy.util;

import android.content.Context;

import com.gxy.GxyApplication;


public class ContextUtils {
    public static Context get() {
        return GxyApplication.getInstance().getApplicationContext();
    }
}
