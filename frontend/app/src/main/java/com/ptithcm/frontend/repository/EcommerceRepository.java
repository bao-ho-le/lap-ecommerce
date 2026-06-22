package com.ptithcm.frontend.repository;

import androidx.annotation.NonNull;
import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.*;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EcommerceRepository {
    private static EcommerceRepository instance;
    private final ApiService apiService;

    private EcommerceRepository() {
        apiService = ApiClient.getApiService();
    }

    public static synchronized EcommerceRepository getInstance() {
        if (instance == null) instance = new EcommerceRepository();
        return instance;
    }

    public void getProducts(String q, Integer catId, Integer brandId, String sort, @NonNull RepositoryCallback<List<ProductDto>> callback) {
        apiService.getProducts(q, catId, brandId, sort).enqueue(new Callback<List<ProductDto>>() {
            @Override
            public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<List<ProductDto>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProductById(Long id, @NonNull RepositoryCallback<ProductDto> callback) {
        apiService.getProductById(id).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getCategories(@NonNull RepositoryCallback<List<CategoryDto>> callback) {
        apiService.getCategories().enqueue(new Callback<List<CategoryDto>>() {
            @Override
            public void onResponse(Call<List<CategoryDto>> call, Response<List<CategoryDto>> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<List<CategoryDto>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getBrands(@NonNull RepositoryCallback<List<BrandDto>> callback) {
        apiService.getBrands().enqueue(new Callback<List<BrandDto>>() {
            @Override
            public void onResponse(Call<List<BrandDto>> call, Response<List<BrandDto>> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<List<BrandDto>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createOrder(String address, @NonNull RepositoryCallback<OrderResponseDto> callback) {
        apiService.createOrder(new OrderRequestDto(address)).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void payCod(Long orderId, @NonNull RepositoryCallback<PaymentResponseDto> callback) {
        apiService.payCod(new PaymentRequestDto(orderId)).enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void payVNPay(Long orderId, @NonNull RepositoryCallback<PaymentResponseDto> callback) {
        apiService.payVNPay(new PaymentRequestDto(orderId)).enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onError("Error: " + response.code());
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
