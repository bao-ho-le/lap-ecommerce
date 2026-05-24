package com.ptithcm.frontend.repository;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.OrderRequestDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private static OrderRepository instance;

    private final ApiService apiService;

    private OrderRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public void createOrder(@NonNull OrderRequestDto request, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.createOrder(request).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response.code(), "Failed to create order"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to create order"));
            }
        });
    }

    public void getOrders(@NonNull RepositoryCallback<List<OrderResponseDto>> callback) {
        apiService.getOrders().enqueue(new Callback<List<OrderResponseDto>>() {
            @Override
            public void onResponse(Call<List<OrderResponseDto>> call, Response<List<OrderResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response.code(), "Failed to load orders"));
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponseDto>> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to load orders"));
            }
        });
    }

    public void getOrderById(long id, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.getOrderById(id).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response.code(), "Failed to load order"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to load order"));
            }
        });
    }

    public void cancelOrder(long id, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.cancelOrder(id).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(errorMessage(response.code(), "Failed to cancel order"));
                }
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to cancel order"));
            }
        });
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
