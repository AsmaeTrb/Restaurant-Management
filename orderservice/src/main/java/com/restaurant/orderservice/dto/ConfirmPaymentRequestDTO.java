package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmPaymentRequestDTO {
    @NotNull
    private PaymentStatus status;
}
