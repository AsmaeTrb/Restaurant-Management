package com.restaurant.payment_service.controller;


import com.restaurant.payment_service.dto.CreatePaymentRequest;
import com.restaurant.payment_service.dto.ConfirmPaymentRequest;
import com.restaurant.payment_service.dto.PaymentResponse;
import com.restaurant.payment_service.enums.PaymentStatus;
import com.restaurant.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")

public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping
    public PaymentResponse create(@Valid @RequestBody CreatePaymentRequest req) {
        return service.create(req);
    }

    @PutMapping("/{paymentId}/confirm")
    public PaymentResponse confirm(@PathVariable String paymentId,
                                   @Valid @RequestBody ConfirmPaymentRequest req) {
        return service.confirm(paymentId, req.getStatus());
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getByOrderId(@PathVariable String orderId) {
        return service.getByOrderId(orderId);
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getById(@PathVariable String paymentId) {
        return service.getById(paymentId);
    }
}
