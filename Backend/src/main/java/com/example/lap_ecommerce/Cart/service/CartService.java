package com.example.lap_ecommerce.Cart.service;

import com.example.lap_ecommerce.Cart.dto.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.CartResponse;
import com.example.lap_ecommerce.Cart.dto.UpdateCartQuantityRequest;

public interface CartService {
    CartResponse getCart(String email);

    CartResponse addToCart(String email, CartAddRequest request);

    CartResponse updateQuantity(String email, Integer cartId, UpdateCartQuantityRequest request);

    CartResponse deleteItem(String email, Integer cartId);

    void clearCart(String email);
}