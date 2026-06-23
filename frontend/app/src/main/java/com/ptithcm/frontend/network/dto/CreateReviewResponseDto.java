package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

// Response from POST /products/{productId}/reviews
public class CreateReviewResponseDto {

    @SerializedName("reviewId")
    public Long reviewId;

    @SerializedName("comment")
    public String comment;

    @SerializedName("rating")
    public Integer rating;

    @SerializedName("createdAt")
    public String createdAt;
}
