package com.restaurant.menuservice.dto;
import lombok.*;

@Data
@AllArgsConstructor
public class PlatResponseDTO {

    private Long id;
    private String nom;
    private double prix;
    private boolean disponible;
    private String categorieNom;
    private String imageUrl; // ← AJOUTEZ CETTE LIGNE
    private Integer stockQuantity; // ← AJOUT: quantité en stock
    private Boolean stockAvailable; // ← AJOUT: disponibilité du stock


}
