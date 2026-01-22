package com.restaurant.orderservice.dto;

import com.restaurant.orderservice.enums.PaymentStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private Double amount;
    private String currency;
    private PaymentStatus status; // ✅ plus String

}
