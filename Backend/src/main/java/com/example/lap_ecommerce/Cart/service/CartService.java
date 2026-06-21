package com.example.lap_ecommerce.Cart.service;

import com.example.lap_ecommerce.Cart.dto.request.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.response.CartResponse;
import com.example.lap_ecommerce.Cart.dto.request.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.entity.Cart;

public interface CartService {
    CartResponse getCart();

    Cart createCartIfNotExists(Long userId);

    CartResponse addToCart(CartAddRequest request);

    CartResponse updateQuantity(Long cartId, UpdateCartQuantityRequest request);

    CartResponse deleteItem(Long cartId);

    void clearCart();
}