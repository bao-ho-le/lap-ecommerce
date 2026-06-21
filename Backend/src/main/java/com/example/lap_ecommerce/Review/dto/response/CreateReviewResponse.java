package com.example.lap_ecommerce.Review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewResponse {
    private Long reviewId;
    private String comment;
    private Integer rating;
    private LocalDateTime createdAt;
}
