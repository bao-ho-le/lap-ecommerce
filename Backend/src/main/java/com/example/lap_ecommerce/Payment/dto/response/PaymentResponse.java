package com.example.lap_ecommerce.Payment.dto.response;

import com.example.lap_ecommerce.Payment.entity.PaymentMethod;
import com.example.lap_ecommerce.Payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long paymentId,

        Long orderId,

        PaymentMethod method,

        BigDecimal amount,

        PaymentStatus status,

        String transactionId,

        String paymentUrl,

        LocalDateTime paidAt

) {
}