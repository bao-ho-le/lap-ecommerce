package com.example.lap_ecommerce.auth.service;

import com.example.lap_ecommerce.auth.dto.AuthResponse;
import com.example.lap_ecommerce.auth.dto.LoginRequest;
import com.example.lap_ecommerce.auth.dto.RegisterRequest;
import com.example.lap_ecommerce.auth.dto.UserProfileDto;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserProfileDto getProfile(String email);
    UserProfileDto updateProfile(String email, UserProfileDto request);
}
