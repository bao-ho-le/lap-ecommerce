package com.ptithcm.frontend.ui.profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ptithcm.frontend.network.dto.UserProfileDto;
import com.ptithcm.frontend.repository.AuthRepository;

public class ProfileViewModel extends ViewModel {
    private AuthRepository authRepository;

    private final MutableLiveData<UserProfileDto> profileResult = new MutableLiveData<>();
    private final MutableLiveData<String> profileError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel() {
        authRepository = null;
    }

    public LiveData<UserProfileDto> getProfileResult() {
        return profileResult;
    }

    public LiveData<String> getProfileError() {
        return profileError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchProfile(Context context) {

        if (authRepository == null) {
            authRepository = AuthRepository.getInstance(context);
        }

        isLoading.setValue(true);
        authRepository.getProfile(new AuthRepository.AuthCallback<UserProfileDto>() {
            @Override
            public void onSuccess(UserProfileDto result) {
                isLoading.setValue(false);
                profileResult.setValue(result);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                profileError.setValue(error);
            }
        });
    }

    public void updateProfile(Context context, UserProfileDto request) {
        if (authRepository == null) {
            authRepository = AuthRepository.getInstance(context);
        }

        isLoading.setValue(true);
        authRepository.updateProfile(request, new AuthRepository.AuthCallback<UserProfileDto>() {
            @Override
            public void onSuccess(UserProfileDto result) {
                isLoading.setValue(false);
                profileResult.setValue(result);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                profileError.setValue(error);
            }
        });
    }
}