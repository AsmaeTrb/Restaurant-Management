package com.example.payment_service.service;

import com.example.payment_service.dto.CreatePaymentRequest;
import com.example.payment_service.entity.Payment;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.repository.PaymentRepository;
import com.example.payment_service.exception.PaymentAlreadyExistsException;
import com.example.payment_service.exception.PaymentNotFoundException;
import com.example.payment_service.exception.PaymentAlreadyProcessedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository repository;

    public PaymentService(PaymentRepository repository) {
        this.repository = repository;
    }

    //  Create payment
    public Payment createPayment(CreatePaymentRequest request) {

        // 1 Vérifier si un paiement existe déjà pour cette commande
        repository.findByOrderId(request.getOrderId())
                .ifPresent(p -> {
                    throw new PaymentAlreadyExistsException(
                            "Payment already exists for orderId: " + request.getOrderId()
                    );
                });

        // 2 Créer le paiement
        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        return repository.save(payment);
    }

    //  Confirm payment
    public Payment confirmPayment(String paymentId, PaymentStatus status) {

        Payment payment = repository.findById(UUID.fromString(paymentId))
                .orElseThrow(() ->
                        new PaymentNotFoundException("Payment not found with id: " + paymentId)
                );

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentAlreadyProcessedException("Payment already processed");
        }

        payment.setStatus(status);
        return repository.save(payment);
    }
    public Payment getByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException("Payment not found for orderId " + orderId));
    }

}


