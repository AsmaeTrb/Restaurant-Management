package com.restaurant.orderservice.kafka;

import com.restaurant.orderservice.dto.PaymentResultStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResultEvent {
    private String orderId;
    private String paymentId;
    private PaymentResultStatus status; // SUCCESS ou FAILED
}


