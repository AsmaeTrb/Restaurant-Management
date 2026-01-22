package com.restaurant.orderservice.dto;


import com.restaurant.orderservice.enums.PaymentMethod;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequestDTO {
    private String orderId;
    private Double amount;
    private String currency;
    private PaymentMethod paymentMethod;
}
