package com.ptithcm.frontend;

import android.app.Application;

public class ECommerceApp extends Application {
    private static ECommerceApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ECommerceApp getInstance() {
        return instance;
    }
}
