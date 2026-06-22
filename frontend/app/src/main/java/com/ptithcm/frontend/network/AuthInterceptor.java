package com.ptithcm.frontend.network;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.utils.SharedPrefsManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SharedPrefsManager sharedPrefsManager;

    public AuthInterceptor(Context context) {
        this.sharedPrefsManager = new SharedPrefsManager(context.getApplicationContext());
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request originalRequest = chain.request();

        String path = originalRequest.url().encodedPath();

        // skip auth APIs
        if (path.contains("/auth/login") || path.contains("/auth/register")) {
            return chain.proceed(originalRequest);
        }

        String token = sharedPrefsManager.getToken();

        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}