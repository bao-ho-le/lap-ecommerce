package com.ptithcm.frontend.network.dto;

public class AuthResponseDto {
    private String accessToken;
    private String tokenType;
    private UserProfileDto user;

    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public UserProfileDto getUser() { return user; }
}
