package com.example.lap_ecommerce.Cart.service;

import com.example.lap_ecommerce.Cart.dto.request.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.response.CartResponse;
import com.example.lap_ecommerce.Cart.dto.request.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.entity.Cart;

public interface CartService {
    CartResponse getCart(String email);

    Cart createCartIfNotExists(Long userId);

    CartResponse addToCart(String email, CartAddRequest request);

    CartResponse updateQuantity(String email, Long cartItemId, UpdateCartQuantityRequest request);

    CartResponse deleteItem(String email, Long cartItemId);

    void clearCart(String email);
}