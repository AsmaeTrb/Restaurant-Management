package com.example.payment_service.kafka;

import com.example.payment_service.dto.CreatePaymentRequest;
import com.example.payment_service.enums.PaymentMethod;
import com.example.payment_service.kafka.event.OrderCreatedEvent;
import com.example.payment_service.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "payment-service"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {

        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .orderId(event.getOrderId())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .paymentMethod(PaymentMethod.CARD)
                .build();

        paymentService.createPayment(request);
    }
}



