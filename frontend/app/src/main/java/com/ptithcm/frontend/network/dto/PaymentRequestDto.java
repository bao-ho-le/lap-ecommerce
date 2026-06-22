package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class PaymentRequestDto {
    @SerializedName("orderId")
    public Long orderId;

    public PaymentRequestDto(Long orderId) {
        this.orderId = orderId;
    }
}
