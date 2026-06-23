package com.example.lap_ecommerce.Review.service.impl;

import com.example.lap_ecommerce.order.service.OrderService;
import com.example.lap_ecommerce.product.entity.Product;
import com.example.lap_ecommerce.product.repository.ProductRepository;
import com.example.lap_ecommerce.Review.dto.request.CreateReviewRequest;
import com.example.lap_ecommerce.Review.dto.response.CreateReviewResponse;
import com.example.lap_ecommerce.Review.dto.response.ProductReviewResponse;
import com.example.lap_ecommerce.Review.entity.Review;
import com.example.lap_ecommerce.Review.repository.ReviewRepository;
import com.example.lap_ecommerce.Review.service.ReviewService;
import com.example.lap_ecommerce.exception.ForbiddenException;
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
    private final OrderService orderService;

    @Override
    public Page<ProductReviewResponse> getProductReviews(Long productId, Pageable pageable) {

        return reviewRepository.findByProductId(productId, pageable)
                .map(review -> ProductReviewResponse.builder()
                        .reviewId(review.getId())
                        .userId(review.getUser().getId())
                        .productId(productId)
                        .comment(review.getComment())
                        .rating(review.getRating())
                        .fullName(review.getUser().getFullName())
                        .createAt(review.getCreatedAt())
                        .build());
    }

    @Override
    public CreateReviewResponse createReview(String email, Long productId, CreateReviewRequest createReviewRequest) {

        if (!orderService.hasUserPurchasedProduct(email, productId)) {
            throw new ForbiddenException("You must purchase this product to review");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

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
    public void deleteReview(String email, Long reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with id: " + reviewId));

        if (!review.getUser().getEmail().equals(email)) {
            throw new ForbiddenException("You are not allowed to delete this review");
        }

        reviewRepository.delete(review);
    }

}
