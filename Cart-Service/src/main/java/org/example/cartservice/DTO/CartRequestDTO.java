package org.example.cartservice.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRequestDTO {
    private String sessionId;      // Optionnel
    private Long customerId;       // Optionnel (si utilisateur connecté)
}