package com.ptithcm.frontend.network;

import com.ptithcm.frontend.network.dto.*;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @GET("products")
    Call<List<ProductDto>> getProducts(
            @Query("q") String query,
            @Query("categoryId") Integer categoryId,
            @Query("brandId") Integer brandId,
            @Query("sort") String sort
    );

    @GET("products/{id}")
    Call<ProductDto> getProductById(@Path("id") Long id);

    @GET("categories")
    Call<List<CategoryDto>> getCategories();

    @GET("brands")
    Call<List<BrandDto>> getBrands();

    @POST("orders")
    Call<OrderResponseDto> createOrder(@Body OrderRequestDto body);

    @POST("payments/cod")
    Call<PaymentResponseDto> payCod(@Body PaymentRequestDto body);

    @POST("payments/vnpay")
    Call<PaymentResponseDto> payVNPay(@Body PaymentRequestDto body);
}
