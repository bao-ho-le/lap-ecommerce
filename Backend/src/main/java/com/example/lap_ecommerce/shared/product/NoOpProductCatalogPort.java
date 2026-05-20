package com.example.lap_ecommerce.shared.product;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NoOpProductCatalogPort implements ProductCatalogPort {

    @Override
    public Optional<ProductSnapshot> findById(Long productId) {
        return Optional.empty();
    }

    @Override
    public void deductStock(Long productId, Integer quantity) {
        // no-op fallback until a real product adapter is wired in
    }

    @Override
    public void restoreStock(Long productId, Integer quantity) {
        // no-op fallback until a real product adapter is wired in
    }
}