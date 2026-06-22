package com.ptithcm.frontend.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.network.dto.ProductDto;
import com.ptithcm.frontend.repository.EcommerceRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;
import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private final EcommerceRepository repository;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ProductViewModel() {
        repository = EcommerceRepository.getInstance();
    }

    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    public void loadProducts(String q, Integer catId, Integer brandId) {
        loading.setValue(true);
        repository.getProducts(q, catId, brandId, null, new RepositoryCallback<List<ProductDto>>() {
            @Override
            public void onSuccess(List<ProductDto> result) {
                List<Product> list = new ArrayList<>();
                for (ProductDto dto : result) {
                    Product p = new Product(dto.id, dto.name, dto.categoryName, dto.price, dto.imageUrl);
                    p.cpu = dto.cpu;
                    p.ram = dto.ramGb != null ? dto.ramGb + "GB" : "";
                    p.storage = dto.storageGb != null ? dto.storageGb + "GB" : "";
                    p.description = dto.description;
                    p.brand = dto.brandName;
                    p.rating = dto.avgRating;
                    list.add(p);
                }
                products.postValue(list);
                loading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }
}
