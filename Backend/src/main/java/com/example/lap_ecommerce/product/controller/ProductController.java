package com.example.lap_ecommerce.product.controller;

import com.example.lap_ecommerce.product.dto.request.ProductFilterRequest;
import com.example.lap_ecommerce.product.dto.response.ProductResponse;
import com.example.lap_ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts(

            @RequestParam(required = false) String q,

            @RequestParam(required = false) Long categoryId,

            @RequestParam(required = false) Long brandId,

            @RequestParam(required = false) String sort,

            @RequestParam(defaultValue = "0") Integer page,

            @RequestParam(defaultValue = "10") Integer size

    ) {

        ProductFilterRequest request = new ProductFilterRequest();

        request.setQ(q);
        request.setCategoryId(categoryId);
        request.setBrandId(brandId);
        request.setSort(sort);
        request.setPage(page);
        request.setSize(size);

        return ResponseEntity.ok(
                productService.getProducts(request)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }
}