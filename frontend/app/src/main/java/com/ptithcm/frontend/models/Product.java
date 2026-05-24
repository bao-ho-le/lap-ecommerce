package com.ptithcm.frontend.models;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {

    private final long id;
    private final String name;
    private final String category;
    private final BigDecimal price;
    private final String cpu;
    private final String ram;
    private final String description;
    private final int imageResId;
    private final String imageUrl;
    private final boolean featured;

    public Product(long id, String name, String category, BigDecimal price, String cpu, String ram, String description, int imageResId, boolean featured) {
        this(id, name, category, price, cpu, ram, description, imageResId, null, featured);
    }

    public Product(long id, String name, String category, BigDecimal price, String cpu, String ram, String description, int imageResId, String imageUrl, boolean featured) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.cpu = cpu;
        this.ram = ram;
        this.description = description;
        this.imageResId = imageResId;
        this.imageUrl = imageUrl;
        this.featured = featured;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCpu() {
        return cpu;
    }

    public String getRam() {
        return ram;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isFeatured() {
        return featured;
    }
}