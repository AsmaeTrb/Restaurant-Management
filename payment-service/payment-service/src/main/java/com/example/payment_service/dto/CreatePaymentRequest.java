package com.example.payment_service.dto;

import com.example.payment_service.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotBlank(message = "orderId is required")
    private String orderId;

    @NotNull
    @Positive(message = "amount must be positive")
    private Double amount;

    @NotBlank
    private String currency;

    @NotNull
    private PaymentMethod paymentMethod;
}



