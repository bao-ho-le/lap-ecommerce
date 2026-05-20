package com.example.lap_ecommerce.Cart.service.impl;

import com.example.lap_ecommerce.Cart.dto.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.CartItemResponse;
import com.example.lap_ecommerce.Cart.dto.CartResponse;
import com.example.lap_ecommerce.Cart.dto.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.entity.Cart;
import com.example.lap_ecommerce.Cart.repository.CartRepository;
import com.example.lap_ecommerce.Cart.service.CartService;
import com.example.lap_ecommerce.exception.OutOfStockException;
import com.example.lap_ecommerce.exception.ResourceNotFoundException;
import com.example.lap_ecommerce.shared.product.ProductCatalogPort;
import com.example.lap_ecommerce.shared.product.ProductSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private static final long DEFAULT_USER_ID = 1L;

    private final CartRepository cartRepository;
    private final ProductCatalogPort productCatalogPort;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart() {
        return buildCartResponse(cartRepository.findAll());
    }

    @Override
    public CartResponse addToCart(CartAddRequest request) {
        ProductSnapshot product = getExistingProduct(request.getProductId());
        validateStock(product, request.getQuantity());

        Cart cartItem = cartRepository.findByProductId(request.getProductId())
                .map(existing -> {
                    int newQuantity = existing.getQuantity() + request.getQuantity();
                    validateStock(product, newQuantity);
                    existing.setQuantity(newQuantity);
                    return existing;
                })
                .orElseGet(() -> Cart.builder()
                        .userId(DEFAULT_USER_ID)
                        .productId(request.getProductId())
                        .quantity(request.getQuantity())
                        .build());

        cartRepository.save(cartItem);
        return getCart();
    }

    @Override
    public CartResponse updateQuantity(Integer cartId, UpdateCartQuantityRequest request) {
        Cart cartItem = findCartItem(cartId);
        ProductSnapshot product = getExistingProduct(cartItem.getProductId());
        validateStock(product, request.getQuantity());

        cartItem.setQuantity(request.getQuantity());
        cartRepository.save(cartItem);
        return getCart();
    }

    @Override
    public CartResponse deleteItem(Integer cartId) {
        Cart cartItem = findCartItem(cartId);
        cartRepository.delete(cartItem);
        return getCart();
    }

    @Override
    public void clearCart() {
        cartRepository.deleteAll();
    }

    private Cart findCartItem(Integer cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartId));
    }

    private ProductSnapshot getExistingProduct(Long productId) {
        return productCatalogPort.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }

    private void validateStock(ProductSnapshot product, Integer requestedQuantity) {
        if (requestedQuantity > product.getStockQty()) {
            throw new OutOfStockException("Requested quantity exceeds available stock for product id: " + product.getId());
        }
    }

    private CartResponse buildCartResponse(List<Cart> cartItems) {
        List<CartItemResponse> items = cartItems.stream()
                .map(this::toCartItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalCartAmount(total)
                .build();
    }

    private CartItemResponse toCartItemResponse(Cart cartItem) {
        ProductSnapshot product = getExistingProduct(cartItem.getProductId());
        BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .cartId(cartItem.getCartId())
                .productId(product.getId())
                .productName(product.getName())
                .unitPrice(product.getPrice())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }
}