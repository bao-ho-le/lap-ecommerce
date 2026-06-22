package com.ptithcm.frontend.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.ptithcm.frontend.models.Product;
import com.ptithcm.frontend.network.dto.ProductDto;
import com.ptithcm.frontend.repository.EcommerceRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;

public class ProductDetailViewModel extends ViewModel {
    private final EcommerceRepository repository;
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ProductDetailViewModel() {
        repository = EcommerceRepository.getInstance();
    }

    public LiveData<Product> getProduct() { return product; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    public void loadProduct(Long id) {
        loading.setValue(true);
        repository.getProductById(id, new RepositoryCallback<ProductDto>() {
            @Override
            public void onSuccess(ProductDto dto) {
                Product p = new Product(dto.id, dto.name, dto.categoryName, dto.price, dto.imageUrl);
                p.cpu = dto.cpu;
                p.ram = dto.ramGb != null ? dto.ramGb + "GB" : "";
                p.storage = dto.storageGb != null ? dto.storageGb + "GB" : "";
                p.description = dto.description;
                p.brand = dto.brandName;
                p.rating = dto.avgRating;
                product.postValue(p);
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
