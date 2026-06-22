package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class BrandDto {
    @SerializedName("brandId")
    public Integer brandId;

    @SerializedName("name")
    public String name;
    @SerializedName("logoUrl")
    public String logoUrl;
}
