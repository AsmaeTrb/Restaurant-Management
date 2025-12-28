package com.restaurant.menuservice.service;

import com.restaurant.menuservice.dto.PromotionRequestDTO;
import com.restaurant.menuservice.dto.PromotionResponseDTO;
import com.restaurant.menuservice.entity.Plat;
import com.restaurant.menuservice.entity.Promotion;
import com.restaurant.menuservice.exception.ResourceNotFoundException;
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

    public PromotionResponseDTO createPromotion(PromotionRequestDTO dto) {
        Promotion promotion = PromotionMapper.toEntity(dto);
        Plat plat = platRepository.findById(dto.getPlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));
        promotion.setPlat(plat);
        return PromotionMapper.toResponse(promotionRepository.save(promotion));
    }

    public List<PromotionResponseDTO> getAllPromotions() {
        return promotionRepository.findAll()
                .stream()
                .map(PromotionMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PromotionResponseDTO updatePromotion(Long id, PromotionRequestDTO dto) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée"));
        promotion.setPourcentage(dto.getPourcentage());
        promotion.setDateDebut(dto.getDateDebut());
        promotion.setDateFin(dto.getDateFin());
        promotion.setActive(dto.isActive());

        Plat plat = platRepository.findById(dto.getPlatId())
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));
        promotion.setPlat(plat);

        return PromotionMapper.toResponse(promotionRepository.save(promotion));
    }

    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée"));
        promotionRepository.delete(promotion);
    }
}




