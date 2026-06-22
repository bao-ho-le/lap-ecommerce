package com.example.lap_ecommerce.Review.service.impl;

import com.example.lap_ecommerce.product.entity.Product;
import com.example.lap_ecommerce.product.repository.ProductRepository;
import com.example.lap_ecommerce.Review.dto.request.CreateReviewRequest;
import com.example.lap_ecommerce.Review.dto.response.CreateReviewResponse;
import com.example.lap_ecommerce.Review.dto.response.ProductReviewResponse;
import com.example.lap_ecommerce.Review.entity.Review;
import com.example.lap_ecommerce.Review.repository.ReviewRepository;
import com.example.lap_ecommerce.Review.service.ReviewService;
import com.example.lap_ecommerce.exception.ResourceNotFoundException;
import com.example.lap_ecommerce.user.entity.User;
import com.example.lap_ecommerce.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ProductReviewResponse> getProductReviews(Long productId, Pageable pageable){

        return reviewRepository.findByProductId(productId, pageable)
                .map(review -> ProductReviewResponse.builder()
                        .comment(review.getComment())
                        .rating(review.getRating())
                        .fullName(review.getUser().getFullName())
                        .createAt(review.getCreatedAt())
                        .build());
    }

    @Override
    public CreateReviewResponse createReview(Long productId, CreateReviewRequest createReviewRequest){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = userRepository.findById(createReviewRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Review newReview = Review.builder()
                .product(product)
                .user(user)
                .rating(createReviewRequest.getRating())
                .comment(createReviewRequest.getComment())
                .build();

        Review savedReview = reviewRepository.save(newReview);

        return CreateReviewResponse.builder()
                .reviewId(savedReview.getId())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with id: " + reviewId));

        reviewRepository.delete(review);
    }

}
