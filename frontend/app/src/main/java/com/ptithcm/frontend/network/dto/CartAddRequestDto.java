package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class CartAddRequestDto {

    @SerializedName("productId")
    public Long productId;

    @SerializedName("quantity")
    public Integer quantity;

    public CartAddRequestDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
