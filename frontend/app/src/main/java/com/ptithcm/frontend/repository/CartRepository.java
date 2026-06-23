package com.ptithcm.frontend.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.CartAddRequestDto;
import com.ptithcm.frontend.network.dto.CartItemDto;
import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.network.dto.UpdateQuantityRequestDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {

    private static final String TAG = "CART_DEBUG";

    private static CartRepository instance;

    private final ApiService apiService;

    private CartRepository(Context context) {
        apiService = ApiClient.getApiService(context.getApplicationContext());
    }

    public static synchronized CartRepository getInstance(Context context) {
        if (instance == null) {
            instance = new CartRepository(context);
        }
        return instance;
    }

    public void getCart(@NonNull RepositoryCallback<CartResponseDto> callback) {
        apiService.getCart().enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                Log.d(TAG, "getCart Code = " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                    return;
                }

                // Backend returns 404 when the user has no cart record yet — treat as empty cart
                if (response.code() == 404) {
                    logErrorBody(response);
                    Log.d(TAG, "No cart found for user, returning empty cart");
                    callback.onSuccess(emptyCart());
                    return;
                }

                logErrorBody(response);
                callback.onError(extractError(response.code(), "Failed to load cart"));
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                Log.d(TAG, "getCart failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to load cart"));
            }
        });
    }

    public void addToCart(long productId, int quantity, @NonNull RepositoryCallback<CartResponseDto> callback) {
        apiService.addToCart(new CartAddRequestDto(productId, quantity)).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                Log.d(TAG, "addToCart Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                } else {
                    logErrorBody(response);
                    callback.onError(extractError(response.code(), "Failed to add item to cart"));
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                Log.d(TAG, "addToCart failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to add item to cart"));
            }
        });
    }

    public void updateQuantity(long cartItemId, int quantity, @NonNull RepositoryCallback<CartResponseDto> callback) {
        Log.d(TAG, "Updating cartItemId=" + cartItemId + " quantity=" + quantity);
        apiService.updateQuantity(cartItemId, new UpdateQuantityRequestDto(quantity)).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                Log.d(TAG, "URL = " + call.request().url());
                Log.d(TAG, "Method = " + call.request().method());
                Log.d(TAG, "Response code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                } else {
                    logErrorBody(response);
                    callback.onError(extractError(response.code(), "Failed to update quantity"));
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                Log.d(TAG, "updateQuantity failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to update quantity"));
            }
        });
    }

    public void deleteItem(long cartItemId, @NonNull RepositoryCallback<CartResponseDto> callback) {
        Log.d(TAG, "Deleting cartItemId=" + cartItemId);
        apiService.deleteItem(cartItemId).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                Log.d(TAG, "URL = " + call.request().url());
                Log.d(TAG, "Method = " + call.request().method());
                Log.d(TAG, "Response code=" + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(normalize(response.body()));
                } else {
                    logErrorBody(response);
                    callback.onError(extractError(response.code(), "Failed to delete cart item"));
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                Log.d(TAG, "deleteItem failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to delete cart item"));
            }
        });
    }

    public void clearCart(@NonNull RepositoryCallback<Void> callback) {
        apiService.clearCart().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d(TAG, "clearCart Code = " + response.code());
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    logErrorBody(response);
                    callback.onError(extractError(response.code(), "Failed to clear cart"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d(TAG, "clearCart failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to clear cart"));
            }
        });
    }

    private CartResponseDto emptyCart() {
        CartResponseDto empty = new CartResponseDto();
        empty.items = new ArrayList<>();
        empty.totalCartAmount = BigDecimal.ZERO;
        return empty;
    }

    private CartResponseDto normalize(CartResponseDto response) {
        CartResponseDto normalized = new CartResponseDto();
        normalized.items = response.items == null
                ? new ArrayList<>()
                : new ArrayList<>(response.items);

        normalized.totalCartAmount = response.totalCartAmount == null
                ? BigDecimal.ZERO
                : response.totalCartAmount;

        for (CartItemDto item : normalized.items) {
            if (item != null) {
                Log.d(TAG, "Cart item cartId=" + item.cartId + " productId=" + item.productId + " qty=" + item.quantity);
            }
        }

        return normalized;
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
