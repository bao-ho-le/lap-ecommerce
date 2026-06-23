package com.ptithcm.frontend.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.OrderRequestDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private static final String TAG = "ORDER_API";

    private static OrderRepository instance;

    private final ApiService apiService;

    private OrderRepository(Context context) {
        apiService = ApiClient.getApiService(context.getApplicationContext());
    }

    public static synchronized OrderRepository getInstance(Context context) {
        if (instance == null) {
            instance = new OrderRepository(context);
        }
        return instance;
    }

    public void createOrder(@NonNull OrderRequestDto request, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.createOrder(request).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                Log.d(TAG, "createOrder URL = " + call.request().url());
                Log.d(TAG, "createOrder Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    logErrorBody(response);
                    callback.onError(errorMessage(response.code(), "Failed to create order"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                Log.d(TAG, "createOrder failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to create order"));
            }
        });
    }

    public void getOrders(@NonNull RepositoryCallback<List<OrderResponseDto>> callback) {
        apiService.getOrders().enqueue(new Callback<List<OrderResponseDto>>() {
            @Override
            public void onResponse(Call<List<OrderResponseDto>> call, Response<List<OrderResponseDto>> response) {
                Log.d(TAG, "getOrders URL = " + call.request().url());
                Log.d(TAG, "getOrders Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    logErrorBody(response);
                    callback.onError(errorMessage(response.code(), "Failed to load orders"));
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponseDto>> call, Throwable t) {
                Log.d(TAG, "getOrders failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to load orders"));
            }
        });
    }

    public void getOrderById(long id, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.getOrderById(id).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                Log.d(TAG, "getOrderById URL = " + call.request().url());
                Log.d(TAG, "getOrderById Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    logErrorBody(response);
                    callback.onError(errorMessage(response.code(), "Failed to load order"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                Log.d(TAG, "getOrderById failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to load order"));
            }
        });
    }

    public void cancelOrder(long id, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.cancelOrder(id).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                Log.d(TAG, "cancelOrder Code = " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    logErrorBody(response);
                    callback.onError(errorMessage(response.code(), "Failed to cancel order"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                Log.d(TAG, "cancelOrder failure = " + t.getMessage());
                callback.onError(messageOrFallback(t, "Failed to cancel order"));
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

    private String messageOrFallback(Throwable throwable, String fallback) {
        if (throwable == null || throwable.getMessage() == null || throwable.getMessage().trim().isEmpty()) {
            return fallback;
        }
        return throwable.getMessage();
    }

    private String errorMessage(int code, String fallback) {
        return fallback + " (HTTP " + code + ")";
    }
}
