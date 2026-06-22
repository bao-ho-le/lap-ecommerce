package com.example.lap_ecommerce.Cart.controller;

import com.example.lap_ecommerce.Cart.dto.request.CartAddRequest;
import com.example.lap_ecommerce.Cart.dto.response.CartResponse;
import com.example.lap_ecommerce.Cart.dto.request.UpdateCartQuantityRequest;
import com.example.lap_ecommerce.Cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.getCart(email));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(Authentication authentication, @Valid @RequestBody CartAddRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.addToCart(email, request));
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<CartResponse> updateQuantity(Authentication authentication, @PathVariable Long id,
                                                       @Valid @RequestBody UpdateCartQuantityRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.updateQuantity(email, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CartResponse> deleteItem(Authentication authentication, @PathVariable Long id) {
        String email = authentication.getName();
        return ResponseEntity.ok(cartService.deleteItem(email, id));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String email = authentication.getName();
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }
}