package org.example.cartservice.DTO;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishInfoDTO {
    private Long id;
    private String nom;
    private double prix;
    private boolean disponible;
    private String categorieNom;
    private String imageUrl; // ← AJOUTEZ CETTE LIGNE
    private Integer stockQuantity;     // ✅
    private Boolean stockAvailable;    // ✅

}