package com.restaurant.menuservice.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatRequestDTO {

    private String nom;
    private double prix;
    private boolean disponible;
    private Long categorieId;
    private String imageUrl; // ← AJOUTEZ CETTE LIGNE
    private Integer initialQuantity; // ✅ nouveau champ


}
