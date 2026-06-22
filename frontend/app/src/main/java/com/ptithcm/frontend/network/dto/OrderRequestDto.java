package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class OrderRequestDto {
    @SerializedName("shippingAddress")
    public String shippingAddress;

    public OrderRequestDto(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
