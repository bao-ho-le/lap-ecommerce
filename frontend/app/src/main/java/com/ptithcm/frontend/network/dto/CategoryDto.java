package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class CategoryDto {
    @SerializedName("categoryId")
    public Integer categoryId;

    @SerializedName("name")
    public String name;
    @SerializedName("description")
    public String description;
}
