package com.example.lap_ecommerce.order.repository;

import com.example.lap_ecommerce.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	java.util.List<Order> findByUserId(Long userId);
}