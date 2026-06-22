package com.example.lap_ecommerce.payment.service;

import com.example.lap_ecommerce.payment.dto.request.CreateCodPaymentRequest;
import com.example.lap_ecommerce.payment.dto.request.CreateVnPayPaymentRequest;
import com.example.lap_ecommerce.payment.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse createCodPayment(CreateCodPaymentRequest request);

    PaymentResponse createVnPayPayment(CreateVnPayPaymentRequest request);

    PaymentResponse handleCallback(String txnRef, String status);
}