package com.example.payment_service.dto;

import com.example.payment_service.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Schema(description = "Requête de confirmation de paiement")
public class ConfirmPaymentRequest {

    @Schema(
            description = "Nouveau statut du paiement",
            example = "SUCCESS"
    )
    @NotNull
    private PaymentStatus status;
}

