package com.restaurant.menuservice.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequestDTO {

    private double pourcentage;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    private Long platId;
}


