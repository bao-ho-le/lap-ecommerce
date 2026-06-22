package com.ptithcm.frontend.network.dto;

public class RegisterRequestDto {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;

    public RegisterRequestDto(String fullName, String email, String password, String phone, String address) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }
}
