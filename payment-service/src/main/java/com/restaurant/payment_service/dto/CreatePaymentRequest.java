package com.restaurant.payment_service.dto;



import com.restaurant.payment_service.enums.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// dto/CreatePaymentRequest.java
public class CreatePaymentRequest {
    @NotBlank private String orderId;
    @NotNull @Positive private Double amount;
    @NotBlank private String currency;
    @NotNull private PaymentMethod paymentMethod;
}


