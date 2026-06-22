package com.example.lap_ecommerce.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private UserProfileDto user;

    public AuthResponse(String accessToken, UserProfileDto user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
