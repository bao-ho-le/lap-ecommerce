package com.example.lap_ecommerce.Order.dto;

import com.example.lap_ecommerce.Order.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    @NotBlank
    private String shippingAddress;

    @NotNull
    private PaymentMethod paymentMethod;
}