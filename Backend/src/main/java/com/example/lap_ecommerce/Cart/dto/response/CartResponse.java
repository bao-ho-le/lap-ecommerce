package com.example.lap_ecommerce.Cart.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CartResponse {
    private List<CartItemResponse> items;
    private BigDecimal totalCartAmount;
}