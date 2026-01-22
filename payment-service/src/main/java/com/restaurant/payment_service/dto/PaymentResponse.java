package com.restaurant.payment_service.dto;



import com.restaurant.payment_service.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// dto/PaymentResponse.java
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private Double amount;
    private String currency;
    private PaymentStatus status;
}

