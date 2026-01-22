package com.restaurant.payment_service.service;

import com.restaurant.payment_service.dto.CreatePaymentRequest;
import com.restaurant.payment_service.dto.PaymentResponse;
import com.restaurant.payment_service.entity.Payment;
import com.restaurant.payment_service.enums.PaymentStatus;
import com.restaurant.payment_service.repository.PaymentRepository;
import com.restaurant.payment_service.exception.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) { this.repo = repo; }

    // Ajouter userId en paramètre
    public PaymentResponse create(CreatePaymentRequest req, Long userId) {
        repo.findByOrderId(req.getOrderId()).ifPresent(p -> {
            throw new PaymentAlreadyExistsException("Payment already exists for orderId=" + req.getOrderId());
        });

        Payment payment = Payment.builder()
                .orderId(req.getOrderId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .paymentMethod(req.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .userId(userId) // Sauvegarder userId
                .build();

        return toResponse(repo.save(payment));
    }

    // Garder userId en paramètre
    public PaymentResponse confirm(String paymentId, PaymentStatus status, Long userId) {
        Payment payment = repo.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        // Vérifier que le payment appartient à l'utilisateur
        if (!payment.getUserId().equals(userId)) {
            throw new UnauthorizedPaymentAccessException("Not authorized to confirm this payment");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentAlreadyProcessedException("Payment already processed");
        }

        if (status == PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Invalid status: PENDING");
        }

        payment.setStatus(status);
        return toResponse(repo.save(payment));
    }

    // Ajouter userId en paramètre
    public PaymentResponse getByOrderId(String orderId, Long userId) {
        Payment payment = repo.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for orderId=" + orderId));

        // Vérifier que le payment appartient à l'utilisateur
        if (!payment.getUserId().equals(userId)) {
            throw new UnauthorizedPaymentAccessException("Not authorized to access this payment");
        }

        return toResponse(payment);
    }

    // Ajouter userId en paramètre
    public PaymentResponse getById(String paymentId, Long userId) {
        Payment payment = repo.findById(UUID.fromString(paymentId))
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        // Vérifier que le payment appartient à l'utilisateur
        if (!payment.getUserId().equals(userId)) {
            throw new UnauthorizedPaymentAccessException("Not authorized to access this payment");
        }

        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getId().toString())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .status(p.getStatus())
                .userId(p.getUserId()) // AJOUTEZ CETTE LIGNE
                .build();
    }
}