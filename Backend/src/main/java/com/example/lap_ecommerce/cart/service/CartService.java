package com.example.lap_ecommerce.cart.service;

import com.example.lap_ecommerce.cart.dto.request.CartAddRequest;
import com.example.lap_ecommerce.cart.dto.response.CartResponse;
import com.example.lap_ecommerce.cart.dto.request.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.cart.entity.Cart;

public interface CartService {
    CartResponse getCart(String email);

    Cart createCartIfNotExists(Long userId);

    CartResponse addToCart(String email, CartAddRequest request);

    CartResponse updateQuantity(String email, Long cartItemId, UpdateCartQuantityRequest request);

    CartResponse deleteItem(String email, Long cartItemId);

    void clearCart(String email);
}