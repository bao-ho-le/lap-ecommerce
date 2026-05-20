package com.example.lap_ecommerce.Cart.repository;

import com.example.lap_ecommerce.Cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByProductId(Long productId);
}