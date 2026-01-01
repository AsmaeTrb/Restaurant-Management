package com.restaurant.menuservice.dto;
import lombok.*;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionResponseDTO {

    private Long id;
    private double pourcentage;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    private String platNom;
}
