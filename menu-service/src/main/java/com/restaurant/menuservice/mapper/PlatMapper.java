package com.restaurant.menuservice.mapper;

import com.restaurant.menuservice.client.StockClient;
import com.restaurant.menuservice.dto.PlatRequestDTO;
import com.restaurant.menuservice.dto.PlatResponseDTO;
import com.restaurant.menuservice.entity.Plat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlatMapper {

    // Entity -> ResponseDTO (sans stock - pour usage interne)
    public static PlatResponseDTO toResponse(Plat plat) {
        // Cette version met stockQuantity et stockAvailable à null !
        return new PlatResponseDTO(
                plat.getId(),
                plat.getNom(),
                plat.getPrix(),
                plat.isDisponible(),
                plat.getCategorie() != null ? plat.getCategorie().getNom() : "Non catégorisé",
                plat.getImageUrl(),
                null,  // ← PROBLEME ICI !
                null   // ← PROBLEME ICI !
        );
    }
    // Entity -> ResponseDTO avec infos stock
    public static PlatResponseDTO toResponse(Plat plat, Integer stockQuantity, Boolean stockAvailable) {
        return new PlatResponseDTO(
                plat.getId(),
                plat.getNom(),
                plat.getPrix(),
                plat.isDisponible(),
                plat.getCategorie() != null ? plat.getCategorie().getNom() : "Non catégorisé",
                plat.getImageUrl(),
                stockQuantity,
                stockAvailable
        );
    }

    // RequestDTO -> Entity (sans catégorie)
    public static Plat toEntity(PlatRequestDTO dto) {
        Plat plat = new Plat();
        plat.setNom(dto.getNom());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());
        plat.setImageUrl(dto.getImageUrl());
        return plat;
    }
}