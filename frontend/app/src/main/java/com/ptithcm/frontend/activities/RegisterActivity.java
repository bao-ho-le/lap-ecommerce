package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ptithcm.frontend.databinding.ActivityRegisterBinding;
import com.ptithcm.frontend.network.dto.RegisterRequestDto;
import com.ptithcm.frontend.ui.auth.AuthViewModel;
import com.ptithcm.frontend.utils.TokenManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        tokenManager = new TokenManager(this);

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterRequestDto request = new RegisterRequestDto(fullName, email, password, phone, address);
            authViewModel.register(request);
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnRegister.setEnabled(!isLoading);
            if (isLoading) {
                binding.btnRegister.setText("");
            } else {
                binding.btnRegister.setText("Register");
            }
        });

        authViewModel.getAuthResult().observe(this, authResponseDto -> {
            if (authResponseDto != null && authResponseDto.getAccessToken() != null) {
                tokenManager.saveToken(authResponseDto.getAccessToken());
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                
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
