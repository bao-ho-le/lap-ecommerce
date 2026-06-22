package com.ptithcm.frontend.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ptithcm.frontend.network.dto.UserProfileDto;
import com.ptithcm.frontend.repository.AuthRepository;

public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<UserProfileDto> profileResult = new MutableLiveData<>();
    private final MutableLiveData<String> profileError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ProfileViewModel() {
        authRepository = AuthRepository.getInstance();
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

    public void fetchProfile() {
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

    public void updateProfile(UserProfileDto request) {
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