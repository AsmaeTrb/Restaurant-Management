package com.example.payment_service.controller;

import com.example.payment_service.dto.ConfirmPaymentRequest;
import com.example.payment_service.dto.CreatePaymentRequest;
import com.example.payment_service.entity.Payment;
import com.example.payment_service.enums.PaymentStatus;
import com.example.payment_service.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/payments")
@Tag(
        name = "Payments",
        description = "API de gestion des paiements"
)
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @Operation(
            summary = "Créer un paiement",
            description = "Crée un paiement avec le statut PENDING"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public Payment create(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return service.createPayment(request);
    }

    @Operation(
            summary = "Confirmer un paiement",
            description = "Met à jour le statut du paiement (SUCCESS ou FAILED)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement confirmé"),
            @ApiResponse(responseCode = "404", description = "Paiement introuvable")
    })
    @PutMapping("/{id}/confirm")
    public Payment confirmPayment(
            @PathVariable String id,
            @Valid @RequestBody ConfirmPaymentRequest request
    ) {
        return service.confirmPayment(id, request.getStatus());
    }
    @GetMapping("/order/{orderId}")
    public Payment getByOrderId(@PathVariable String orderId) {
        return service.getByOrderId(orderId);
    }

    @PutMapping("/{id}/cancel")
    public Payment cancel(@PathVariable String id) {
        return service.confirmPayment(id, PaymentStatus.FAILED);
    }
}


