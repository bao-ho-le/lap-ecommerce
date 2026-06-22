package com.ptithcm.frontend.network.dto;

public class UserProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String role;

    public UserProfileDto() {}
    public UserProfileDto(String fullName, String phone, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getRole() { return role; }
}
