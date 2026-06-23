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

    private static final String TAG = "AUTH";

    private final SharedPrefsManager sharedPrefsManager;

    public AuthInterceptor(Context context) {
        this.sharedPrefsManager = new SharedPrefsManager(context.getApplicationContext());
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        Request originalRequest = chain.request();
        String path = originalRequest.url().encodedPath();

        Log.d("CART_API", "URL = " + originalRequest.url());
        Log.d("CART_API", "Method = " + originalRequest.method());

        // Public endpoints — no Authorization header needed
        if (path.contains("/auth/login") || path.contains("/auth/register")) {
            return chain.proceed(originalRequest);
        }

        String token = sharedPrefsManager.getToken();
        Log.d(TAG, "Token exists = " + (token != null && !token.isEmpty()));

        if (token != null && !token.isEmpty()) {
            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            Response response = chain.proceed(authenticatedRequest);
            Log.d("CART_API", "Code = " + response.code());
            return response;
        }

        Response response = chain.proceed(originalRequest);
        Log.d("CART_API", "Code = " + response.code());
        return response;
    }
}
