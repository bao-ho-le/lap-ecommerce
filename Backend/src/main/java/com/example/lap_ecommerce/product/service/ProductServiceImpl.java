package com.example.lap_ecommerce.product.service;

import com.example.lap_ecommerce.product.dto.request.ProductFilterRequest;
import com.example.lap_ecommerce.product.dto.response.ProductResponse;
import com.example.lap_ecommerce.product.entity.Product;
import com.example.lap_ecommerce.product.repository.ProductRepository;

import com.example.lap_ecommerce.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> getProducts(ProductFilterRequest request) {

        List<Product> products = productRepository.findAll();

        // search theo tên
        if (request.getQ() != null && !request.getQ().isBlank()) {
            String keyword = request.getQ().toLowerCase();

            products = products.stream()
                    .filter(product ->
                            product.getName() != null
                                    && product.getName()
                                    .toLowerCase()
                                    .contains(keyword))
                    .toList();
        }

        // lọc category
        if (request.getCategoryId() != null) {
            products = products.stream()
                    .filter(product ->
                            product.getCategory() != null
                                    && product.getCategory()
                                    .getCategoryId()
                                    .equals(request.getCategoryId()))
                    .toList();
        }

        // lọc brand
        if (request.getBrandId() != null) {
            products = products.stream()
                    .filter(product ->
                            product.getBrand() != null
                                    && product.getBrand()
                                    .getBrandId()
                                    .equals(request.getBrandId()))
                    .toList();
        }

        // sort
        if ("price_asc".equalsIgnoreCase(request.getSort())) {

            products = products.stream()
                    .sorted(Comparator.comparing(Product::getPrice))
                    .toList();

        } else if ("price_desc".equalsIgnoreCase(request.getSort())) {

            products = products.stream()
                    .sorted(
                            Comparator.comparing(Product::getPrice)
                                    .reversed()
                    )
                    .toList();
        }

        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: " + id));

        return toResponse(product);
    }

    private ProductResponse toResponse(Product product) {

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQty(product.getStockQty())
                .imageUrl(product.getImageUrl())
                .cpu(product.getCpu())
                .ramGb(product.getRamGb())
                .storageGb(product.getStorageGb())
                .os(product.getOs())
                .avgRating(product.getAvgRating())
                .categoryId(product.getCategory().getCategoryId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getBrandId())
                .brandName(product.getBrand().getName())
                .build();
    }
}