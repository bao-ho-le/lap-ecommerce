package com.example.lap_ecommerce.Order.dto.response;

import com.example.lap_ecommerce.Order.entity.OrderStatus;
import com.example.lap_ecommerce.Order.entity.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private PaymentMethod paymentMethod;
    private LocalDateTime orderDate;
    private List<OrderItemResponse> items;
}