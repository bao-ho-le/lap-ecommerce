package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.ptithcm.frontend.databinding.ActivitySplashBinding;
import com.ptithcm.frontend.utils.SharedPrefsManager;

public class SplashActivity extends BaseActivity {

    private ActivitySplashBinding binding;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPrefsManager = new SharedPrefsManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (sharedPrefsManager.hasToken()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 1500);

        binding.btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            // We do not finish() here so the user can back out to Splash
        });

        binding.btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
        });
    }
}