package com.ptithcm.frontend.models;

public class Brand {

    private Long brandId;

    private String name;

    private String logoUrl;

    public Brand() {
    }

    public Brand(Long brandId, String name, String logoUrl) {
        this.brandId = brandId;
        this.name = name;
        this.logoUrl = logoUrl;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }
}
