package com.ptithcm.frontend.network.dto;

import com.google.gson.annotations.SerializedName;

public class ProductReviewResponse {

    @SerializedName(value = "reviewId", alternate = {"id"})
    public Long reviewId;

    @SerializedName("comment")
    public String comment;

    @SerializedName("rating")
    public Integer rating;

    @SerializedName("userId")
    public Long userId;

    @SerializedName("fullName")
    public String fullName;

    @SerializedName("productId")
    public Long productId;

    @SerializedName("createAt")
    public String createAt;
}
