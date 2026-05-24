package com.example.lap_ecommerce.Cart.controller;

import com.example.lap_ecommerce.Cart.dto.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.CartResponse;
import com.example.lap_ecommerce.Cart.dto.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartAddRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<CartResponse> updateQuantity(@PathVariable Integer id,
                                                       @Valid @RequestBody UpdateCartQuantityRequest request) {
        return ResponseEntity.ok(cartService.updateQuantity(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CartResponse> deleteItem(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.deleteItem(id));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}