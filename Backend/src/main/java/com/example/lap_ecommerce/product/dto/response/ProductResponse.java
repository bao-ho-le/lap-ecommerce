package com.example.lap_ecommerce.product.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(

        Long id,

        String name,

        String description,

        BigDecimal price,

        Integer stockQty,

        String imageUrl,

        String cpu,

        Integer ramGb,

        Integer storageGb,

        String os,

        Float avgRating,

        Long categoryId,

        String categoryName,

        Long brandId,

        String brandName

) {
}