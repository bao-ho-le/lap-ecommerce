package com.ptithcm.frontend.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.ptithcm.frontend.network.dto.OrderResponseDto;
import com.ptithcm.frontend.network.dto.PaymentResponseDto;
import com.ptithcm.frontend.repository.EcommerceRepository;
import com.ptithcm.frontend.repository.RepositoryCallback;

public class OrderViewModel extends ViewModel {
    private final EcommerceRepository repository;
    private final MutableLiveData<OrderResponseDto> orderResponse = new MutableLiveData<>();
    private final MutableLiveData<PaymentResponseDto> paymentResponse = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public OrderViewModel() {
        repository = EcommerceRepository.getInstance();
    }

    public LiveData<OrderResponseDto> getOrderResponse() { return orderResponse; }
    public LiveData<PaymentResponseDto> getPaymentResponse() { return paymentResponse; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    public void placeOrder(String address, String paymentMethod) {
        loading.setValue(true);
        repository.createOrder(address, new RepositoryCallback<OrderResponseDto>() {
            @Override
            public void onSuccess(OrderResponseDto result) {
                orderResponse.postValue(result);
                if ("COD".equals(paymentMethod)) {
                    processCod(result.id);
                } else {
                    processVNPay(result.id);
                }
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    private void processCod(Long orderId) {
        repository.payCod(orderId, new RepositoryCallback<PaymentResponseDto>() {
            @Override
            public void onSuccess(PaymentResponseDto result) {
                paymentResponse.postValue(result);
                loading.postValue(false);
            }

            @Override
            public void onError(String message) {
                error.postValue(message);
                loading.postValue(false);
            }
        });
    }

    private void processVNPay(Long orderId) {
        repository.payVNPay(orderId, new RepositoryCallback<PaymentResponseDto>() {
            @Override
            public void onSuccess(PaymentResponseDto result) {
                paymentResponse.postValue(result);
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
