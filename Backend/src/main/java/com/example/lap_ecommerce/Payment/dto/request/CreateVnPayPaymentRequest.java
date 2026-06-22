package com.example.lap_ecommerce.Payment.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateVnPayPaymentRequest(

        @NotNull
        Long orderId

) {
}