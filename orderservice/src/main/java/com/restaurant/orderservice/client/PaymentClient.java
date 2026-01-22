package com.restaurant.orderservice.client;

import com.restaurant.orderservice.dto.CreatePaymentRequestDTO;
import com.restaurant.orderservice.dto.ConfirmPaymentRequestDTO; // Ajouté
import com.restaurant.orderservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "${payment-service.url}")
public interface PaymentClient {
    @PostMapping("/payments")
    PaymentResponse createPayment(@RequestBody CreatePaymentRequestDTO request);

    @PutMapping("/payments/{paymentId}/confirm")
    PaymentResponse confirmPayment(@PathVariable String paymentId,
                                      @RequestBody ConfirmPaymentRequestDTO request);

    @GetMapping("/payments/order/{orderId}")
    PaymentResponse getByOrderId(@PathVariable String orderId);
}
