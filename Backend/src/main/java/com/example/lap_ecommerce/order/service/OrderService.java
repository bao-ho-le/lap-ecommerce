package com.example.lap_ecommerce.order.service;

import com.example.lap_ecommerce.order.dto.request.OrderRequest;
import com.example.lap_ecommerce.order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(String email, OrderRequest request);

    List<OrderResponse> getAllOrders(String email);

    OrderResponse getOrderById(String email, Long id);

    OrderResponse cancelOrder(String email, Long id);
}