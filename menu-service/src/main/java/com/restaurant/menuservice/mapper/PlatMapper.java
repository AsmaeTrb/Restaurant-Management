package com.restaurant.menuservice.mapper;

import com.restaurant.menuservice.dto.PlatRequestDTO;
import com.restaurant.menuservice.dto.PlatResponseDTO;
import com.restaurant.menuservice.entity.Plat;

public class PlatMapper {

    // Entity -> ResponseDTO
    public static PlatResponseDTO toResponse(Plat plat) {
        return new PlatResponseDTO(
                plat.getId(),
                plat.getNom(),
                plat.getPrix(),
                plat.isDisponible(),
                plat.getCategorie().getNom(),
                plat.getImageUrl() // ← AJOUTEZ L'IMAGE ICI

        );
    }

    // RequestDTO -> Entity (sans catégorie)
    public static Plat toEntity(PlatRequestDTO dto) {
        Plat plat = new Plat();
        plat.setNom(dto.getNom());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());
        plat.setImageUrl(dto.getImageUrl()); // ← AJOUTEZ L'IMAGE ICI
        return plat;
    }
}


