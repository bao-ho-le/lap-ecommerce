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
import com.ptithcm.frontend.utils.SharedPrefsManager;
import com.ptithcm.frontend.database.DatabaseHelper;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    private ProfileViewModel profileViewModel;
    private SharedPrefsManager sharedPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        sharedPrefsManager = new SharedPrefsManager(this);

        binding.backButton.setOnClickListener(v -> finish());

        // Pre-fill from SharedPrefsManager
        UserProfileDto savedUser = sharedPrefsManager.getUser();
        if (savedUser != null) {
            if (savedUser.getEmail() != null) binding.tvEmail.setText(savedUser.getEmail());
            if (savedUser.getFullName() != null) binding.etFullName.setText(savedUser.getFullName());
            if (savedUser.getPhone() != null) binding.etPhone.setText(savedUser.getPhone());
            if (savedUser.getAddress() != null) binding.etAddress.setText(savedUser.getAddress());
        }

        // Fetch profile
        profileViewModel.fetchProfile(this);

        binding.btnSave.setOnClickListener(v -> {
            String fullName = binding.etFullName.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                showToast("Please fill in all fields");
                return;
            }

            UserProfileDto request = new UserProfileDto(fullName, phone, address);
            profileViewModel.updateProfile(this, request);
        });

        binding.btnLogout.setOnClickListener(v -> {
            sharedPrefsManager.clearSession();
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.clearOrders();
            showToast("Logged out successfully");
            Intent intent = new Intent(this, LoginActivity.class);
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
                
                
                // Update User Info in SharedPrefsManager
                sharedPrefsManager.saveUser(userProfileDto);
                
                // Show toast if button is enabled meaning we came from save
                if (binding.btnSave.isEnabled()) {
                     showToast("Profile updated");
                }
            }
        });

        profileViewModel.getProfileError().observe(this, error -> {
            if (error != null) {
                showToast(error);
                // Logout on auth failure
                if (error.contains("401") || error.contains("403")) {
                    binding.btnLogout.performClick();
                }
            }
        });
    }
}
