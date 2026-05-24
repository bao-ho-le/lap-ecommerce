package com.ptithcm.frontend.ui.orders;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ptithcm.frontend.models.OrderSummary;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.repository.MockEcommerceRepository;
import com.ptithcm.frontend.repository.RealEcommerceRepository;

import java.util.List;

public class OrdersViewModel extends AndroidViewModel {

    private final MutableLiveData<List<OrderSummary>> orders = new MutableLiveData<>();
    private final RealEcommerceRepository realRepository;
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public OrdersViewModel(@NonNull Application application) {
        super(application);
        realRepository = RealEcommerceRepository.getInstance();
        // load from backend
        fetchOrders();
    }

    public LiveData<List<OrderSummary>> getOrders() {
        return orders;
    }

    public LiveData<Boolean> getLoading() { return loading; }

    public LiveData<String> getError() { return error; }

    public void fetchOrders() {
        loading.setValue(true);
        realRepository.fetchOrders(new RealEcommerceRepository.RepoCallback<java.util.List<OrderResponseDto>>() {
            @Override
            public void onSuccess(java.util.List<OrderResponseDto> result) {
                loading.postValue(false);
                java.util.List<OrderSummary> list = new java.util.ArrayList<>();
                if (result != null) {
                    for (OrderResponseDto dto : result) {
                        OrderSummary s = new OrderSummary(
                                dto.id == null ? 0L : dto.id,
                                "#" + (dto.id == null ? "0" : dto.id.toString()),
                                dto.status == null ? "" : dto.status,
                                dto.orderDate == null ? "" : dto.orderDate,
                                dto.totalAmount == null ? java.math.BigDecimal.ZERO : dto.totalAmount,
                                dto.items == null ? 0 : dto.items.size()
                        );
                        list.add(s);
                    }
                }
                orders.postValue(list);
            }

            @Override
            public void onError(String message) {
                loading.postValue(false);
                error.postValue(message);
            }
        });
    }
}