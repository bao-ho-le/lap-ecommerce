package com.ptithcm.frontend.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.CreateReviewRequestDto;
import com.ptithcm.frontend.network.dto.CreateReviewResponseDto;
import com.ptithcm.frontend.network.dto.PageResponse;
import com.ptithcm.frontend.network.dto.ProductReviewResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {

    private static final String TAG = "REVIEW_DEBUG";

    private static ReviewRepository instance;
    private final ApiService apiService;

    private ReviewRepository(Context context) {
        apiService = ApiClient.getApiService(context.getApplicationContext());
    }

    public static synchronized ReviewRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewRepository(context);
        }
        return instance;
    }

    public void getReviews(long productId, int page, int size,
                           @NonNull RepositoryCallback<PageResponse<ProductReviewResponse>> callback) {

        apiService.getProductReviews(productId, page, size).enqueue(new Callback<PageResponse<ProductReviewResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<ProductReviewResponse>> call,
                                   Response<PageResponse<ProductReviewResponse>> response) {
                Log.d(TAG, "getProductReviews URL = " + call.request().url());
                Log.d(TAG, "getProductReviews Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<ProductReviewResponse> body = response.body();
                    if (body.content == null) {
                        body.content = java.util.Collections.emptyList();
                    }
                    callback.onSuccess(body);
                } else {
                    logErrorBody(response);
                    callback.onError("Failed to load reviews (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<PageResponse<ProductReviewResponse>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Failed to load reviews");
            }
        });
    }

    public void createReview(long productId, int rating, String comment,
                             @NonNull RepositoryCallback<CreateReviewResponseDto> callback) {

        CreateReviewRequestDto request = new CreateReviewRequestDto(rating, comment);

        apiService.createReview(productId, request).enqueue(new Callback<CreateReviewResponseDto>() {
            @Override
            public void onResponse(Call<CreateReviewResponseDto> call, Response<CreateReviewResponseDto> response) {
                Log.d(TAG, "createReview URL = " + call.request().url());
                Log.d(TAG, "createReview Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    logErrorBody(response);
                    callback.onError("Failed to create review (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<CreateReviewResponseDto> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Failed to create review");
            }
        });
    }

    public void deleteReview(long reviewId, @NonNull RepositoryCallback<Void> callback) {
        Log.d(TAG, "Deleting reviewId=" + reviewId);
        apiService.deleteReview(reviewId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "deleteReview URL = " + call.request().url());
                Log.d(TAG, "deleteReview Method = " + call.request().method());
                Log.d(TAG, "deleteReview Code = " + response.code());
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    logErrorBody(response);
                    callback.onError("Failed to delete review (HTTP " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "deleteReview failure = " + t.getMessage());
                callback.onError(t.getMessage() != null ? t.getMessage() : "Failed to delete review");
            }
        });
    }

    private void logErrorBody(Response<?> response) {
        if (response.errorBody() == null) {
            Log.d(TAG, "Error body = null");
            return;
        }
        try {
            Log.d(TAG, "Error body = " + response.errorBody().string());
        } catch (IOException e) {
            Log.d(TAG, "Error body read failed = " + e.getMessage());
        }
    }
}
