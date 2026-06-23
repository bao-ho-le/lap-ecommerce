package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

// Request body for POST /products/{productId}/reviews
public class CreateReviewRequestDto {

    @SerializedName("rating")
    public Integer rating;

    @SerializedName("comment")
    public String comment;

    public CreateReviewRequestDto(Integer rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }
}
