package com.example.lap_ecommerce.product.service;

import com.example.lap_ecommerce.product.dto.request.ProductFilterRequest;
import com.example.lap_ecommerce.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts(ProductFilterRequest request);

    ProductResponse getProductById(Long id);
}