package com.ptithcm.frontend.repository;

import android.content.Context;
import android.os.Looper;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.PageResponse;
import com.ptithcm.frontend.network.dto.ProductReviewResponse;

import java.util.ArrayList;
import java.util.List;
import android.os.Handler;

public class ReviewRepository {
    private static ReviewRepository instance;
    private final ApiService apiService;

    private ReviewRepository(Context context) {
        apiService = ApiClient.getApiService(context);
    }

    public static synchronized ReviewRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ReviewRepository(context);
        }
        return instance;
    }

    public void getReviews(Long productId, int page, int size, RepositoryCallback<PageResponse<ProductReviewResponse>> callback) {

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            List<ProductReviewResponse> list = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                ProductReviewResponse r = new ProductReviewResponse();
                r.reviewId = (long) (page * 10 + i);
                r.productId = productId;
                r.fullName = "User " + i;
                r.comment = "Review fake " + i;
                r.rating = (i % 5) + 1;
                r.userId = 100L + i;
                r.createAt = "2026-06-22";

                list.add(r);
            }

            PageResponse<ProductReviewResponse> response = new PageResponse<>();
            response.content = list;
            response.page = page;
            response.totalPages = 5;
            response.totalElements = 50;

            callback.onSuccess(response);

        }, 500);
    }
}

