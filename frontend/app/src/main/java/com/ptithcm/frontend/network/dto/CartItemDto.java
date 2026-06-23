package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItemDto implements Serializable {

    // Backend may serialize cart line id as cartId or id
    @SerializedName(value = "cartId", alternate = {"id"})
    public Long cartId;

    @SerializedName("productId")
    public Long productId;

    @SerializedName("productName")
    public String productName;

    @SerializedName("unitPrice")
    public BigDecimal unitPrice;

    @SerializedName("quantity")
    public Integer quantity;

    @SerializedName("subtotal")
    public BigDecimal subtotal;
}
