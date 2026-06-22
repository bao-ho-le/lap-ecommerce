package com.ptithcm.frontend.network;

import android.content.Context;
import androidx.annotation.NonNull;

import com.ptithcm.frontend.utils.SharedPrefsManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SharedPrefsManager sharedPrefsManager;

    public AuthInterceptor(Context context) {
        this.sharedPrefsManager = new SharedPrefsManager(context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Skip adding token for login and register endpoints
        if (originalRequest.url().encodedPath().contains("/auth/login") ||
            originalRequest.url().encodedPath().contains("/auth/register")) {
            return chain.proceed(originalRequest);
        }

        String token = sharedPrefsManager.getToken();
        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(originalRequest);
    }
}
