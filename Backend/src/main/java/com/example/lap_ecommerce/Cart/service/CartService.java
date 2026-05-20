package com.example.lap_ecommerce.Cart.service;

import com.example.lap_ecommerce.Cart.dto.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.CartResponse;
import com.example.lap_ecommerce.Cart.dto.UpdateCartQuantityRequest;

public interface CartService {
    CartResponse getCart();

    CartResponse addToCart(CartAddRequest request);

    CartResponse updateQuantity(Integer cartId, UpdateCartQuantityRequest request);

    CartResponse deleteItem(Integer cartId);

    void clearCart();
}