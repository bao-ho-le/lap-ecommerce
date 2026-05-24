package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponseDto {

    @SerializedName("id")
    public Long id;

    @SerializedName("totalAmount")
    public BigDecimal totalAmount;

    @SerializedName("status")
    public String status;

    @SerializedName("shippingAddress")
    public String shippingAddress;

    @SerializedName("paymentMethod")
    public String paymentMethod;

    @SerializedName("orderDate")
    public String orderDate;

    @SerializedName("items")
    public List<CartItemDto> items;
}
