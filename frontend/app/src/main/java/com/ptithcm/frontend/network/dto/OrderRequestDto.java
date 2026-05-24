package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class OrderRequestDto {

    @SerializedName("shippingAddress")
    public String shippingAddress;

    @SerializedName("paymentMethod")
    public String paymentMethod;

    public OrderRequestDto(String shippingAddress, String paymentMethod) {
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
    }
}
