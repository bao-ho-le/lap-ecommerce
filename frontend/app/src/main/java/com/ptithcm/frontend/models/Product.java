package com.ptithcm.frontend.models;

import com.ptithcm.frontend.network.dto.ProductDto;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {

    public Long id;

    public String name;

    public String description;

    public BigDecimal price;

    public Integer stockQty;

    public String imageUrl;

    public String cpu;

    public Integer ramGb;

    public Integer storageGb;

    public String os;

    public Float avgRating;

    public Long categoryId;

    public String categoryName;

    public Long brandId;

    public String brandName;

    public Product() {
    }
    private Product map(ProductDto dto) {

        Product p = new Product();

        p.id = dto.id;
        p.name = dto.name;
        p.description = dto.description;
        p.price = dto.price;
        p.stockQty = dto.stockQty;
        p.imageUrl = dto.imageUrl;
        p.cpu = dto.cpu;
        p.ramGb = dto.ramGb;
        p.storageGb = dto.storageGb;
        p.os = dto.os;
        p.avgRating = dto.avgRating;
        p.categoryId = dto.categoryId;
        p.categoryName = dto.categoryName;
        p.brandId = dto.brandId;
        p.brandName = dto.brandName;

        return p;
    }
}
