package com.restaurant.menuservice.mapper;

import com.restaurant.menuservice.dto.PromotionRequestDTO;
import com.restaurant.menuservice.dto.PromotionResponseDTO;
import com.restaurant.menuservice.entity.Promotion;

public class PromotionMapper {

    // RequestDTO -> Entity
    public static Promotion toEntity(PromotionRequestDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setPourcentage(dto.getPourcentage());
        promotion.setDateDebut(dto.getDateDebut());
        promotion.setDateFin(dto.getDateFin());
        promotion.setActive(dto.isActive());
        return promotion;
    }

    // Entity -> ResponseDTO
    public static PromotionResponseDTO toResponse(Promotion promotion) {
        return new PromotionResponseDTO(
                promotion.getId(),
                promotion.getPourcentage(),
                promotion.getDateDebut(),
                promotion.getDateFin(),
                promotion.isActive(),
                promotion.getPlat() != null ? promotion.getPlat().getNom() : null
        );
    }
}


