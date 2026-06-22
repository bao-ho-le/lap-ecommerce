package com.ptithcm.frontend.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ptithcm.frontend.network.dto.AuthResponseDto;
import com.ptithcm.frontend.network.dto.RegisterRequestDto;
import com.ptithcm.frontend.repository.AuthRepository;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<AuthResponseDto> authResult = new MutableLiveData<>();
    private final MutableLiveData<String> authError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel() {
        authRepository = AuthRepository.getInstance();
    }

    public LiveData<AuthResponseDto> getAuthResult() {
        return authResult;
    }

    public LiveData<String> getAuthError() {
        return authError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void login(String email, String password) {
        isLoading.setValue(true);
        authRepository.login(email, password, new AuthRepository.AuthCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto result) {
                isLoading.setValue(false);
                authResult.setValue(result);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                authError.setValue(error);
            }
        });
    }

    public void register(RegisterRequestDto request) {
        isLoading.setValue(true);
        authRepository.register(request, new AuthRepository.AuthCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto result) {
                isLoading.setValue(false);
                authResult.setValue(result);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                authError.setValue(error);
            }
        });
    }
}
