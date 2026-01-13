package com.example.payment_service.exception;

public class PaymentAlreadyExistsException extends RuntimeException {

    public PaymentAlreadyExistsException(String message) {
        super(message);
    }
}

