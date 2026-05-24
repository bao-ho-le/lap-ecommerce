package com.ptithcm.frontend.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.network.dto.OrderRequestDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.network.dto.UpdateQuantityRequestDto;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealEcommerceRepository {

    private static RealEcommerceRepository instance;
    private final ApiService api;

    private RealEcommerceRepository() {
        api = ApiClient.getApiService();
    }

    // Products
    public void fetchProducts(@NonNull RepoCallback<java.util.List<com.ptithcm.frontend.network.dto.ProductDto>> cb) {
        api.getProducts().enqueue(new Callback<java.util.List<com.ptithcm.frontend.network.dto.ProductDto>>() {
            @Override
            public void onResponse(Call<java.util.List<com.ptithcm.frontend.network.dto.ProductDto>> call, Response<java.util.List<com.ptithcm.frontend.network.dto.ProductDto>> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Failed to load products: " + response.code());
            }

            @Override
            public void onFailure(Call<java.util.List<com.ptithcm.frontend.network.dto.ProductDto>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void fetchProductById(long id, @NonNull RepoCallback<com.ptithcm.frontend.network.dto.ProductDto> cb) {
        api.getProductById(id).enqueue(new Callback<com.ptithcm.frontend.network.dto.ProductDto>() {
            @Override
            public void onResponse(Call<com.ptithcm.frontend.network.dto.ProductDto> call, Response<com.ptithcm.frontend.network.dto.ProductDto> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Failed to load product: " + response.code());
            }

            @Override
            public void onFailure(Call<com.ptithcm.frontend.network.dto.ProductDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public static synchronized RealEcommerceRepository getInstance() {
        if (instance == null) instance = new RealEcommerceRepository();
        return instance;
    }

    public interface RepoCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public void fetchCart(@NonNull RepoCallback<CartResponseDto> cb) {
        api.getCart().enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onError("Failed to load cart: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void updateQuantity(int cartId, int quantity, @NonNull RepoCallback<CartResponseDto> cb) {
        api.updateQuantity(cartId, new UpdateQuantityRequestDto(quantity)).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onError("Failed to update quantity: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void deleteItem(int cartId, @NonNull RepoCallback<CartResponseDto> cb) {
        api.deleteItem(cartId).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onSuccess(response.body());
                } else {
                    cb.onError("Failed to delete item: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void clearCart(@NonNull RepoCallback<Void> cb) {
        api.clearCart().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) cb.onSuccess(null);
                else cb.onError("Failed to clear cart: " + response.code());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void addToCart(Long productId, Integer quantity, @NonNull RepoCallback<CartResponseDto> cb) {
        api.addToCart(new com.ptithcm.frontend.network.dto.CartAddRequestDto(productId, quantity)).enqueue(new Callback<CartResponseDto>() {
            @Override
            public void onResponse(Call<CartResponseDto> call, Response<CartResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Failed to add to cart: " + response.code());
            }

            @Override
            public void onFailure(Call<CartResponseDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    // Orders
    public void fetchOrders(@NonNull RepoCallback<java.util.List<OrderResponseDto>> cb) {
        api.getOrders().enqueue(new Callback<java.util.List<OrderResponseDto>>() {
            @Override
            public void onResponse(Call<java.util.List<OrderResponseDto>> call, Response<java.util.List<OrderResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Failed to load orders: " + response.code());
            }

            @Override
            public void onFailure(Call<java.util.List<OrderResponseDto>> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void fetchOrderById(long id, @NonNull RepoCallback<OrderResponseDto> cb) {
        api.getOrderById(id).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Failed to load order: " + response.code());
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }

    public void placeOrder(OrderRequestDto req, @NonNull RepoCallback<OrderResponseDto> cb) {
        api.createOrder(req).enqueue(new Callback<OrderResponseDto>() {
            @Override
            public void onResponse(Call<OrderResponseDto> call, Response<OrderResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) cb.onSuccess(response.body());
                else cb.onError("Failed to place order: " + response.code());
            }

            @Override
            public void onFailure(Call<OrderResponseDto> call, Throwable t) {
                cb.onError(t.getMessage());
            }
        });
    }
}
