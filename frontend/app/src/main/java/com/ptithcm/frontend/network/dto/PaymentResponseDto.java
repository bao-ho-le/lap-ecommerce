package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class PaymentResponseDto {

    @SerializedName("paymentId")
    public Long paymentId;

    @SerializedName("transactionId")
    public String transactionId;

    @SerializedName("paymentUrl")
    public String paymentUrl;

    @SerializedName("status")
    public String status;
}