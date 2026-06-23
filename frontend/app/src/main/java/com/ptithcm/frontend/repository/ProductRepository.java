package com.ptithcm.frontend.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ptithcm.frontend.R;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.network.ApiClient;
import com.ptithcm.frontend.network.ApiService;
import com.ptithcm.frontend.network.dto.PageResponse;
import com.ptithcm.frontend.network.dto.ProductDto;

import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {

    private static final String TAG = "PRODUCT_DEBUG";

    private static ProductRepository instance;
    private final ApiService apiService;
    private final Gson gson = new Gson();

    private ProductRepository(Context context) {
        apiService = ApiClient.getApiService(context);
    }

    public static synchronized ProductRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProductRepository(context);
        }
        return instance;
    }

    public void getProducts(String q,
                            Long categoryId,
                            Long brandId,
                            String sort,
                            int page,
                            int size,
                            @NonNull RepositoryCallback<List<Product>> callback) {
        apiService.getProducts(normalizeQuery(q), categoryId, brandId, sort, sanitizePage(page), sanitizeSize(size))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d(TAG, "getProducts URL = " + call.request().url());
                        Log.d(TAG, "getProducts Code = " + response.code());
                        if (!response.isSuccessful()) {
                            callback.onError("Error: " + response.code());
                            return;
                        }

                        ResponseBody body = response.body();
                        if (body == null) {
                            callback.onSuccess(new ArrayList<>());
                            return;
                        }

                        try {
                            String json = body.string();
                            callback.onSuccess(parseProducts(json));
                        } catch (Exception e) {
                            callback.onError(messageOrFallback(e, "Failed to load products"));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        callback.onError(messageOrFallback(t, "Failed to load products"));
                    }
                });
    }

    public void getProductById(long id, @NonNull RepositoryCallback<Product> callback) {
        apiService.getProductById(id).enqueue(new Callback<ProductDto>() {
            @Override
            public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                if (!response.isSuccessful()) {
                    callback.onError("Error: " + response.code());
                    return;
                }

                ProductDto body = response.body();
                if (body == null) {
                    callback.onError("Product not found");
                    return;
                }

                Product product = map(body);
                if (product == null) {
                    callback.onError("Product not found");
                } else {
                    callback.onSuccess(product);
                }
            }

            @Override
            public void onFailure(Call<ProductDto> call, Throwable t) {
                callback.onError(messageOrFallback(t, "Failed to load product"));
            }
        });
    }

    private Product map(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        String category = dto.category != null ? dto.category : "";
        String ram = dto.ramGb != null ? dto.ramGb + "GB" : "";
        long id = dto.id != null ? dto.id : 0L;

        return new Product(
                id,
                dto.name != null ? dto.name : "",
                category,
                dto.price,
                dto.cpu,
                ram,
                dto.description != null ? dto.description : "",
                R.drawable.ic_placeholder,
                dto.imageUrl,
                false
        );
    }

    private String normalizeQuery(String q) {
        if (q == null) {
            return null;
        }
        String trimmed = q.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int sanitizePage(int page) {
        return Math.max(page, 0);
    }

    private int sanitizeSize(int size) {
        return size > 0 ? size : 20;
    }

    private String messageOrFallback(Throwable throwable, String fallback) {
        if (throwable == null || throwable.getMessage() == null || throwable.getMessage().trim().isEmpty()) {
            return fallback;
        }
        return throwable.getMessage();
    }

    private List<Product> parseProducts(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String trimmed = json.trim();
        List<ProductDto> dtos = new ArrayList<>();

        try {
            if (trimmed.startsWith("[")) {
                Type listType = new TypeToken<List<ProductDto>>() {}.getType();
                List<ProductDto> list = gson.fromJson(trimmed, listType);
                if (list != null) {
                    dtos.addAll(list);
                }
            } else {
                Type pageType = new TypeToken<PageResponse<ProductDto>>() {}.getType();
                PageResponse<ProductDto> page = gson.fromJson(trimmed, pageType);
                if (page != null && page.content != null) {
                    dtos.addAll(page.content);
                }
            }
        } catch (Exception ignored) {
            Type listType = new TypeToken<List<ProductDto>>() {}.getType();
            List<ProductDto> list = gson.fromJson(trimmed, listType);
            if (list != null) {
                dtos.addAll(list);
            }
        }

        List<Product> products = new ArrayList<>();
        for (ProductDto dto : dtos) {
            Product product = map(dto);
            if (product != null) {
                products.add(product);
            }
        }
        return products;
    }
}
