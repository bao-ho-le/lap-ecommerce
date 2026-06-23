package com.ptithcm.frontend;

import android.app.Application;

import com.ptithcm.frontend.network.ApiClient;

public class ECommerceApp extends Application {
    private static ECommerceApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        // Pre-initialize Retrofit with AuthInterceptor so repositories always use authenticated client
        ApiClient.getRetrofit(this);
    }

    public static ECommerceApp getInstance() {
        return instance;
    }
}
