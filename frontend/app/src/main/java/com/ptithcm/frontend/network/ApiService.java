package com.ptithcm.frontend.network;

import com.ptithcm.frontend.network.dto.CartResponseDto;
import com.ptithcm.frontend.network.dto.OrderRequestDto;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.network.dto.UpdateQuantityRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("cart")
    Call<CartResponseDto> getCart();

    @GET("products")
    Call<java.util.List<com.ptithcm.frontend.network.dto.ProductDto>> getProducts();

    @GET("products/{id}")
    Call<com.ptithcm.frontend.network.dto.ProductDto> getProductById(@Path("id") long id);

    @PUT("cart/{id}/quantity")
    Call<CartResponseDto> updateQuantity(@Path("id") int cartId, @Body UpdateQuantityRequestDto body);

    @DELETE("cart/{id}")
    Call<CartResponseDto> deleteItem(@Path("id") int cartId);

    @DELETE("cart/clear")
    Call<Void> clearCart();

    @POST("orders")
    Call<OrderResponseDto> createOrder(@Body OrderRequestDto body);

    @POST("cart/add")
    Call<CartResponseDto> addToCart(@Body com.ptithcm.frontend.network.dto.CartAddRequestDto body);

    @GET("orders")
    Call<java.util.List<OrderResponseDto>> getOrders();

    @GET("orders/{id}")
    Call<OrderResponseDto> getOrderById(@Path("id") long id);

    @PUT("orders/{id}/cancel")
    Call<OrderResponseDto> cancelOrder(@Path("id") long id);


    @POST("auth/login")
    Call<com.ptithcm.frontend.network.dto.AuthResponseDto> login(@Body com.ptithcm.frontend.network.dto.LoginRequestDto body);

    @POST("auth/register")
    Call<com.ptithcm.frontend.network.dto.AuthResponseDto> register(@Body com.ptithcm.frontend.network.dto.RegisterRequestDto body);

    @GET("auth/profile")
    Call<com.ptithcm.frontend.network.dto.UserProfileDto> getProfile();

    @PUT("auth/profile")
    Call<com.ptithcm.frontend.network.dto.UserProfileDto> updateProfile(@Body com.ptithcm.frontend.network.dto.UserProfileDto body);

}
