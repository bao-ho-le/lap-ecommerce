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
import com.ptithcm.frontend.utils.SharedPrefsManager;

public class RegisterActivity extends BaseActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        sharedPrefsManager = new SharedPrefsManager(this);

        binding.btnRegister.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Please enter a valid email address");
                return;
            }

            if (password.length() < 6) {
                showToast("Password must be at least 6 characters");
                return;
            }

            RegisterRequestDto request = new RegisterRequestDto(fullName, email, password, phone, "");
            authViewModel.register(this, request);
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
                sharedPrefsManager.saveToken(authResponseDto.getAccessToken());
                sharedPrefsManager.saveUser(authResponseDto.getUser());
                sharedPrefsManager.saveEmailToHistory(binding.etEmail.getText().toString().trim());
                showToast("Registration successful");
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        authViewModel.getAuthError().observe(this, error -> {
            if (error != null) {
                showToast(error);
            }
        });
    }
}
