package com.example.lap_ecommerce.Product.repository;

import com.example.lap_ecommerce.Product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}