package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemDto implements Serializable {

    @SerializedName("itemId")
    public Long itemId;

    @SerializedName("productId")
    public Long productId;

    @SerializedName("productName")
    public String productName;

    @SerializedName("quantity")
    public Integer quantity;

    @SerializedName("unitPrice")
    public BigDecimal unitPrice;

    @SerializedName("subtotal")
    public BigDecimal subtotal;
}
