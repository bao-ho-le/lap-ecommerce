package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class ProductDto {

    @SerializedName("id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("category")
    public String category;

    @SerializedName("description")
    public String description;

    @SerializedName("price")
    public BigDecimal price;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("cpu")
    public String cpu;

    @SerializedName("ramGb")
    public Integer ramGb;

    @SerializedName("featured")
    public Boolean featured;
}
