package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ptithcm.frontend.databinding.ActivityLoginBinding;
import com.ptithcm.frontend.ui.auth.AuthViewModel;
import com.ptithcm.frontend.utils.SharedPrefsManager;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sharedPrefsManager = new SharedPrefsManager(this);

        Set<String> emailHistory = sharedPrefsManager.getEmailHistory();
        if (!emailHistory.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>(emailHistory));
            binding.etEmail.setAdapter(adapter);
        }

        binding.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }

            authViewModel.login(this, email, password);
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnLogin.setEnabled(!isLoading);
            if (isLoading) {
                binding.btnLogin.setText("");
            } else {
                binding.btnLogin.setText("Login");
            }
        });

        authViewModel.getAuthResult().observe(this, authResponseDto -> {
            if (authResponseDto != null && authResponseDto.getAccessToken() != null) {
                sharedPrefsManager.saveToken(authResponseDto.getAccessToken());
                sharedPrefsManager.saveUser(authResponseDto.getUser());
                sharedPrefsManager.saveEmailToHistory(binding.etEmail.getText().toString().trim());
                showToast("Login successful");
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                showToast("Invalid credentials");
            }
        });
    }
}
