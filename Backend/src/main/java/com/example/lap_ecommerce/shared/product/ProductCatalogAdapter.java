package com.example.lap_ecommerce.shared.product;

import com.example.lap_ecommerce.product.entity.Product;
import com.example.lap_ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductCatalogAdapter implements ProductCatalogPort {

    private final ProductRepository productRepository;

    @Override
    public Optional<ProductSnapshot> findById(Long productId) {
        return productRepository.findById(productId)
                .map(p -> ProductSnapshot.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .stockQty(p.getStockQty() == null ? 0 : p.getStockQty())
                        .build());
    }

    @Override
    @Transactional
    public void deductStock(Long productId, Integer quantity) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));
        int newStock = (p.getStockQty() == null ? 0 : p.getStockQty()) - quantity;
        p.setStockQty(newStock);
        productRepository.save(p);
    }

    @Override
    @Transactional
    public void restoreStock(Long productId, Integer quantity) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));
        int newStock = (p.getStockQty() == null ? 0 : p.getStockQty()) + quantity;
        p.setStockQty(newStock);
        productRepository.save(p);
    }
}