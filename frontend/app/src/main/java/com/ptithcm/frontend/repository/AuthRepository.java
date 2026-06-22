package com.ptithcm.frontend.repository;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.AuthResponseDto;
import com.ptithcm.frontend.network.dto.LoginRequestDto;
import com.ptithcm.frontend.network.dto.RegisterRequestDto;
import com.ptithcm.frontend.network.dto.UserProfileDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private static AuthRepository instance;
    private final ApiService api;

    private AuthRepository() {
        api = ApiClient.getApiService();
    }

    public static AuthRepository getInstance() {
        if (instance == null) instance = new AuthRepository();
        return instance;
    }

    public interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public void login(String email, String password, AuthCallback<AuthResponseDto> callback) {
        api.login(new LoginRequestDto(email, password)).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponseDto> call, @NonNull Response<AuthResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Login failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponseDto> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(RegisterRequestDto request, AuthCallback<AuthResponseDto> callback) {
        api.register(request).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponseDto> call, @NonNull Response<AuthResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponseDto> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProfile(AuthCallback<UserProfileDto> callback) {
        api.getProfile().enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileDto> call, @NonNull Response<UserProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch profile: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileDto> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void updateProfile(UserProfileDto request, AuthCallback<UserProfileDto> callback) {
        api.updateProfile(request).enqueue(new Callback<UserProfileDto>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileDto> call, @NonNull Response<UserProfileDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update profile: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileDto> call, @NonNull Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
