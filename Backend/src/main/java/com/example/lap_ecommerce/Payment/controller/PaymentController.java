package com.example.lap_ecommerce.Payment.controller;

import com.example.lap_ecommerce.Payment.dto.request.CreateCodPaymentRequest;
import com.example.lap_ecommerce.Payment.dto.request.CreateVnPayPaymentRequest;
import com.example.lap_ecommerce.Payment.dto.response.PaymentResponse;
import com.example.lap_ecommerce.Payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/cod")
    public ResponseEntity<PaymentResponse> createCodPayment(
            @Valid @RequestBody CreateCodPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.createCodPayment(request));
    }

    @PostMapping("/vnpay")
    public ResponseEntity<PaymentResponse> createVnPayPayment(
            @Valid @RequestBody CreateVnPayPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.createVnPayPayment(request));
    }

    @GetMapping("/callback")
    public ResponseEntity<PaymentResponse> handleCallback(
            @RequestParam String txnRef,
            @RequestParam String status
    ) {
        return ResponseEntity.ok(
                paymentService.handleCallback(txnRef, status)
        );
    }
}