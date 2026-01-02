package com.restaurant.menuservice.controller;
import com.restaurant.menuservice.dto.PromotionRequestDTO;
import com.restaurant.menuservice.dto.PromotionResponseDTO;
import com.restaurant.menuservice.service.PromotionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @PostMapping
    public PromotionResponseDTO createPromotion(@RequestBody PromotionRequestDTO dto) {
        return promotionService.createPromotion(dto);
    }

    @GetMapping
    public List<PromotionResponseDTO> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    @PutMapping("/{id}")
    public PromotionResponseDTO updatePromotion(@PathVariable Long id, @RequestBody PromotionRequestDTO dto) {
        return promotionService.updatePromotion(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
    }
}
