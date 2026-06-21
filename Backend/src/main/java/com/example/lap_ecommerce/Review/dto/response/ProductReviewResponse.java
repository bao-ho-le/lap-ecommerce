package com.example.lap_ecommerce.Review.dto.response;


import com.example.lap_ecommerce.Product.entity.Product;
import com.example.lap_ecommerce.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewResponse {

    private Long reviewId;
    private String comment;
    private Integer rating;

    private Long userId;
    private String username;

    private Long productId;

    private LocalDateTime createAt;
}
