package com.example.lap_ecommerce.Order.repository;

import com.example.lap_ecommerce.Order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsByOrder_User_IdAndProduct_Id(Long userId, Long productId);
}