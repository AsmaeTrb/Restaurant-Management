package com.restaurant.orderservice.client;

import com.restaurant.orderservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payment-service", url = "http://localhost:8082")
public interface PaymentClient {

    //  Consulter un paiement par orderId
    @GetMapping("/payments/order/{orderId}")
    PaymentResponse getPaymentByOrderId(@PathVariable String orderId);

    //  Confirmer paiement
    @PutMapping("/payments/{paymentId}/confirm")
    PaymentResponse confirmPayment(@PathVariable String paymentId);

    //  Annuler paiement
    @PutMapping("/payments/{paymentId}/cancel")
    PaymentResponse cancelPayment(@PathVariable String paymentId);
}

