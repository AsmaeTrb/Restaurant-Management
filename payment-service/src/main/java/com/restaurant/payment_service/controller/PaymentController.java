package com.restaurant.payment_service.controller;

import com.restaurant.payment_service.dto.CreatePaymentRequest;
import com.restaurant.payment_service.dto.ConfirmPaymentRequest;
import com.restaurant.payment_service.dto.PaymentResponse;
import com.restaurant.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) { this.service = service; }

    @PostMapping
    public PaymentResponse create(@AuthenticationPrincipal Jwt jwt,
                                  @Valid @RequestBody CreatePaymentRequest req) {
        Long userId = jwt.getClaim("userId");
        return service.create(req, userId); // Passer userId au service
    }

    @PutMapping("/{paymentId}/confirm")
    public PaymentResponse confirm(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable String paymentId,
                                   @Valid @RequestBody ConfirmPaymentRequest req) {
        Long userId = jwt.getClaim("userId");
        return service.confirm(paymentId, req.getStatus(), userId);
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getByOrderId(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable String orderId) {
        Long userId = jwt.getClaim("userId");
        return service.getByOrderId(orderId, userId); // Passer userId
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse getById(@AuthenticationPrincipal Jwt jwt,
                                   @PathVariable String paymentId) {
        Long userId = jwt.getClaim("userId");
        return service.getById(paymentId, userId); // Passer userId
    }
}