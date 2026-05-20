package com.example.lap_ecommerce.shared.product;

import java.util.Optional;

public interface ProductCatalogPort {
    Optional<ProductSnapshot> findById(Long productId);

    void deductStock(Long productId, Integer quantity);

    void restoreStock(Long productId, Integer quantity);
}