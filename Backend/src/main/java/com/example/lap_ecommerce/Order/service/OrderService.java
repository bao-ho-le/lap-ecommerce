package com.example.lap_ecommerce.Order.service;

import com.example.lap_ecommerce.Order.dto.request.OrderRequest;
import com.example.lap_ecommerce.Order.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long id);

    OrderResponse cancelOrder(Long id);
}