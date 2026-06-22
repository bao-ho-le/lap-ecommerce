package com.example.lap_ecommerce.product.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductFilterRequest {

    private String q;

    private Long categoryId;

    private Long brandId;

    private String sort;

    private Integer page = 0;

    private Integer size = 10;
}