package com.example.lap_ecommerce.Review.controller;

import com.example.lap_ecommerce.Review.dto.request.CreateReviewRequest;
import com.example.lap_ecommerce.Review.dto.response.CreateReviewResponse;
import com.example.lap_ecommerce.Review.dto.response.ProductReviewResponse;
import com.example.lap_ecommerce.Review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<Page<ProductReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sortObj = Sort.by(Sort.Order.desc("createdAt"));

        Pageable pageable = PageRequest.of(page, size, sortObj);

        return ResponseEntity.ok(reviewService.getProductReviews(productId, pageable));
    }

    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<CreateReviewResponse> createReview(
            Authentication authentication,
            @PathVariable Long productId,
            @Valid @RequestBody CreateReviewRequest createReviewRequest) {

        String email = authentication.getName();
        return ResponseEntity.ok(reviewService.createReview(email, productId, createReviewRequest));
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
        String email = authentication.getName();
        reviewService.deleteReview(email, id);
        return ResponseEntity.noContent().build();
    }
}
