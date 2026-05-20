package com.example.lap_ecommerce.shared.product;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductSnapshot {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stockQty;
}