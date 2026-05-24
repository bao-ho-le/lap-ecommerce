package com.ptithcm.frontend.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.repository.MockEcommerceRepository;
import com.ptithcm.frontend.repository.RealEcommerceRepository;
import com.ptithcm.frontend.network.dto.ProductDto;
import android.util.Log;

import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MockEcommerceRepository repository;
    private final RealEcommerceRepository realRepository;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categories = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = MockEcommerceRepository.getInstance(application);
        realRepository = RealEcommerceRepository.getInstance();
        categories.setValue(repository.getCategories());
        fetchProducts();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<Boolean> getLoading() { return loading; }

    public LiveData<String> getError() { return error; }

    public void fetchProducts() {
        loading.setValue(true);
        realRepository.fetchProducts(new RealEcommerceRepository.RepoCallback<java.util.List<ProductDto>>() {
            @Override
            public void onSuccess(java.util.List<ProductDto> result) {
                loading.postValue(false);
                java.util.List<Product> list = new java.util.ArrayList<>();
                if (result != null) {
                    for (ProductDto dto : result) {
                        Product p = new Product(
                                dto.id == null ? 0L : dto.id,
                                dto.name == null ? "" : dto.name,
                                dto.category == null ? "" : dto.category,
                                dto.price == null ? java.math.BigDecimal.ZERO : dto.price,
                                dto.cpu == null ? "" : dto.cpu,
                                dto.ramGb == null ? "" : String.valueOf(dto.ramGb),
                                dto.description == null ? "" : dto.description,
                                com.ptithcm.frontend.R.drawable.ic_placeholder,
                                dto.imageUrl,
                                dto.featured == null ? false : dto.featured
                        );
                        list.add(p);
                    }
                }
                products.postValue(list);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }

    public LiveData<List<String>> getCategories() {
        return categories;
    }

    public void filter(String query, String category) {
        products.setValue(repository.searchProducts(query, category));
    }
}