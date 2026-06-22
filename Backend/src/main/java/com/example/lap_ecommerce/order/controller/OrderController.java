package com.example.lap_ecommerce.order.controller;

import com.example.lap_ecommerce.order.dto.request.OrderRequest;
import com.example.lap_ecommerce.order.dto.response.OrderResponse;
import com.example.lap_ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(Authentication authentication, @Valid @RequestBody OrderRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.createOrder(email, request));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getAllOrders(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(Authentication authentication, @PathVariable Long id) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getOrderById(email, id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(Authentication authentication, @PathVariable Long id) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.cancelOrder(email, id));
    }
}