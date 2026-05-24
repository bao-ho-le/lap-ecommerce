package com.example.lap_ecommerce.Order.repository;

import com.example.lap_ecommerce.Order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
	java.util.List<Order> findByUserId(Long userId);
}