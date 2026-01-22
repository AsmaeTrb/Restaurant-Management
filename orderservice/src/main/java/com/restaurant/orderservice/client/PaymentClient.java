package com.restaurant.orderservice.client;

import com.restaurant.orderservice.dto.CreatePaymentRequestDTO;
import com.restaurant.orderservice.dto.ConfirmPaymentRequestDTO; // Ajouté
import com.restaurant.orderservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "http://localhost:8083")
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse createPayment(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CreatePaymentRequestDTO request
    );

    @PutMapping("/payments/{paymentId}/confirm")
    PaymentResponse confirmPayment(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String paymentId,
            @RequestBody ConfirmPaymentRequestDTO request
    );

    @GetMapping("/payments/order/{orderId}")
    PaymentResponse getByOrderId(
            @RequestHeader("Authorization") String authorization,
            @PathVariable String orderId
    );
}
