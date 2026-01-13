package com.restaurant.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotBlank
    private String orderId;

    @NotNull
    @Positive
    private Double amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String paymentMethod; // CARD, CASH, MOBILE
}


