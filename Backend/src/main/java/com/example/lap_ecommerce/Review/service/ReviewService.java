package com.example.lap_ecommerce.Review.service;

import com.example.lap_ecommerce.Review.dto.request.CreateReviewRequest;
import com.example.lap_ecommerce.Review.dto.response.CreateReviewResponse;
import com.example.lap_ecommerce.Review.dto.response.ProductReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ReviewService {
    Page<ProductReviewResponse> getProductReviews(Long productId, Pageable pageable);

    CreateReviewResponse createReview(Long productId, CreateReviewRequest createReviewRequest);

    void deleteReview(Long reviewId);
}
