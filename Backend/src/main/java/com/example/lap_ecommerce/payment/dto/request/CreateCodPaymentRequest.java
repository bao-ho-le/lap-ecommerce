package com.example.lap_ecommerce.payment.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateCodPaymentRequest(

        @NotNull
        Long orderId

) {
}