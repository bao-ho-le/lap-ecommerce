package com.ptithcm.frontend.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ptithcm.frontend.databinding.ActivityProfileBinding;
import com.ptithcm.frontend.network.dto.UserProfileDto;
import com.ptithcm.frontend.ui.profile.ProfileViewModel;
import com.ptithcm.frontend.utils.TokenManager;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        tokenManager = new TokenManager(this);

        // Fetch profile
        profileViewModel.fetchProfile();

        binding.btnSave.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            UserProfileDto request = new UserProfileDto(fullName, phone, address);
            profileViewModel.updateProfile(request);
        });

        binding.btnLogout.setOnClickListener(v -> {
            tokenManager.clearToken();
            Intent intent = new Intent(this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        profileViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnSave.setEnabled(!isLoading);
            if (isLoading) {
                binding.btnSave.setText("");
            } else {
                binding.btnSave.setText("Save Changes");
            }
        });

        profileViewModel.getProfileResult().observe(this, userProfileDto -> {
            if (userProfileDto != null) {
                binding.tvEmail.setText(userProfileDto.getEmail());
                binding.etFullName.setText(userProfileDto.getFullName());
                binding.etPhone.setText(userProfileDto.getPhone());
                binding.etAddress.setText(userProfileDto.getAddress());
                
                // Show toast if button is enabled meaning we came from save
                if (binding.btnSave.isEnabled()) {
                     Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        profileViewModel.getProfileError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                // Logout on auth failure
                if (error.contains("401") || error.contains("403")) {
                    binding.btnLogout.performClick();
                }
            }
        });
    }
}
