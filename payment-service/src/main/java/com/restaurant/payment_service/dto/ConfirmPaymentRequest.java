package com.restaurant.payment_service.dto;



import com.restaurant.payment_service.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmPaymentRequest {
    @NotNull private PaymentStatus status; // COMPLETED ou FAILED
}

