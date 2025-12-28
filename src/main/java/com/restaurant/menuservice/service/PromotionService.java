package com.restaurant.menuservice.service;

import com.restaurant.menuservice.dto.PromotionRequestDTO;
import com.restaurant.menuservice.dto.PromotionResponseDTO;
import com.restaurant.menuservice.entity.Plat;
import com.restaurant.menuservice.entity.Promotion;
import com.restaurant.menuservice.mapper.PromotionMapper;
import com.restaurant.menuservice.repository.PlatRepository;
import com.restaurant.menuservice.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PlatRepository platRepository;

    public PromotionService(PromotionRepository promotionRepository, PlatRepository platRepository) {
        this.promotionRepository = promotionRepository;
        this.platRepository = platRepository;
    }

    // Créer une promotion
    public PromotionResponseDTO createPromotion(PromotionRequestDTO dto) {
        Promotion promotion = PromotionMapper.toEntity(dto);

        // Récupérer le plat depuis la DB et l'associer
        Plat plat = platRepository.findById(dto.getPlatId())
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));
        promotion.setPlat(plat);

        Promotion saved = promotionRepository.save(promotion);
        return PromotionMapper.toResponse(saved);
    }

    // Lister toutes les promotions
    public List<PromotionResponseDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(PromotionMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Mettre à jour une promotion
    public PromotionResponseDTO updatePromotion(Long id, PromotionRequestDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion non trouvée"));

        promotion.setPourcentage(dto.getPourcentage());
        promotion.setDateDebut(dto.getDateDebut());
        promotion.setDateFin(dto.getDateFin());
        promotion.setActive(dto.isActive());

        // Mettre à jour le plat associé
        Plat plat = platRepository.findById(dto.getPlatId())
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));
        promotion.setPlat(plat);

        Promotion updated = promotionRepository.save(promotion);
        return PromotionMapper.toResponse(updated);
    }

    // Supprimer une promotion
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }
}



