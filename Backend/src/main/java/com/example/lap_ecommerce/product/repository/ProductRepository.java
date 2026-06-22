package com.example.lap_ecommerce.product.repository;

import com.example.lap_ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Long> {

}