package com.example.lap_ecommerce.Cart.repository;

import com.example.lap_ecommerce.Cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
}
