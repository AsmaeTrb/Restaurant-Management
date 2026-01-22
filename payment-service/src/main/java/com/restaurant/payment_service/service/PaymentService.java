package com.restaurant.payment_service.service;

import com.restaurant.payment_service.dto.CreatePaymentRequest;
import com.restaurant.payment_service.dto.PaymentResponse;
import com.restaurant.payment_service.entity.Payment;
import com.restaurant.payment_service.enums.PaymentStatus;
import com.restaurant.payment_service.repository.PaymentRepository;
import com.restaurant.payment_service.exception.PaymentAlreadyExistsException;
import com.restaurant.payment_service.exception.PaymentNotFoundException;
import com.restaurant.payment_service.exception.PaymentAlreadyProcessedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) { this.repo = repo; }

    public PaymentResponse create(CreatePaymentRequest req) {
        repo.findByOrderId(req.getOrderId()).ifPresent(p -> {
            throw new RuntimeException("Payment already exists for orderId=" + req.getOrderId());
        });

        Payment payment = Payment.builder()
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .paymentMethod(req.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        return toResponse(repo.save(payment));
    }

    public PaymentResponse confirm(String paymentId, PaymentStatus status) {
        Payment payment = repo.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment already processed");
        }

        if (status == PaymentStatus.PENDING) {
            throw new RuntimeException("Invalid status: PENDING");
        }

        payment.setStatus(status);
        return toResponse(repo.save(payment));
    }

    public PaymentResponse getByOrderId(String orderId) {
        return toResponse(repo.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for orderId=" + orderId)));
    }

    public PaymentResponse getById(String paymentId) {
        return toResponse(repo.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new RuntimeException("Payment not found")));
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getId().toString())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus())
                .build();
    }
}
