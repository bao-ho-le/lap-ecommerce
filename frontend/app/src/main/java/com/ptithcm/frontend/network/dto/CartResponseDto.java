package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;

public class CartResponseDto {

    @SerializedName("items")
    public List<CartItemDto> items;

    @SerializedName("totalCartAmount")
    public BigDecimal totalCartAmount;
}
