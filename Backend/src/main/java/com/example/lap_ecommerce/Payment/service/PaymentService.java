package com.example.lap_ecommerce.Payment.service;

import com.example.lap_ecommerce.Payment.dto.request.CreateCodPaymentRequest;
import com.example.lap_ecommerce.Payment.dto.request.CreateVnPayPaymentRequest;
import com.example.lap_ecommerce.Payment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createCodPayment(CreateCodPaymentRequest request);

    PaymentResponse createVnPayPayment(CreateVnPayPaymentRequest request);

    PaymentResponse handleCallback(String txnRef, String status);
}