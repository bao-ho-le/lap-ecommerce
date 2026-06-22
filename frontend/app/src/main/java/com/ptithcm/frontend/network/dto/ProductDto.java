package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class ProductDto {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("description")
    public String description;

    @SerializedName("price")
    public BigDecimal price;

    @SerializedName("stockQty")
    public Integer stockQty;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("cpu")
    public String cpu;

    @SerializedName("ramGb")
    public Integer ramGb;

    @SerializedName("storageGb")
    public Integer storageGb;

    @SerializedName("os")
    public String os;

    @SerializedName("avgRating")
    public Float avgRating;

    @SerializedName("categoryId")
    public Long categoryId;

    @SerializedName("categoryName")
    public String categoryName;

    @SerializedName("brandId")
    public Long brandId;

    @SerializedName("brandName")
    public String brandName;
}
