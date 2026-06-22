package com.ptithcm.frontend.repository;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.network.dto.UpdateQuantityRequestDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private static CartRepository instance;

    private final ApiService apiService;

    private CartRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }
    public void getCart(@NonNull RepositoryCallback<CartResponseDto> callback) {
        apiService.getCart().enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                } else {
                    callback.onError(extractError(response.code(), "Failed to load cart"));
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to load cart"));
            }
        });
    }

    public void updateQuantity(int cartId, int quantity, @NonNull RepositoryCallback<CartResponseDto> callback) {
        apiService.updateQuantity(cartId, new UpdateQuantityRequestDto(quantity)).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                } else {
                    callback.onError(extractError(response.code(), "Failed to update quantity"));
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to update quantity"));
            }
        });
    }

    public void deleteItem(int cartId, @NonNull RepositoryCallback<CartResponseDto> callback) {
        apiService.deleteItem(cartId).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                } else {
                    callback.onError(extractError(response.code(), "Failed to delete cart item"));
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to delete cart item"));
            }
        });
    }

    private CartResponseDto normalize(CartResponseDto response) {
        CartResponseDto normalized = new CartResponseDto();
        normalized.items = response.items == null ? new ArrayList<>() : new ArrayList<>(response.items);
        normalized.totalCartAmount = response.totalCartAmount == null ? BigDecimal.ZERO : response.totalCartAmount;
        return normalized;
    }

    private String messageOrFallback(Throwable throwable, String fallback) {
        if (throwable == null || throwable.getMessage() == null || throwable.getMessage().trim().isEmpty()) {
            return fallback;
        }
        return throwable.getMessage();
    }

    private String extractError(int code, String fallback) {
        return fallback + " (HTTP " + code + ")";
    }
}
