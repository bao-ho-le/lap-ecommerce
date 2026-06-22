package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ptithcm.frontend.databinding.ActivityLoginBinding;
import com.ptithcm.frontend.ui.auth.AuthViewModel;
import com.ptithcm.frontend.utils.TokenManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        tokenManager = new TokenManager(this);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.login(email, password);
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
                tokenManager.saveToken(authResponseDto.getAccessToken());
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
