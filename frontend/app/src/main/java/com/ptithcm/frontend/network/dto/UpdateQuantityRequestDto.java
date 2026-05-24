package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class UpdateQuantityRequestDto {

    @SerializedName("quantity")
    public Integer quantity;

    public UpdateQuantityRequestDto(Integer quantity) {
        this.quantity = quantity;
    }
}
