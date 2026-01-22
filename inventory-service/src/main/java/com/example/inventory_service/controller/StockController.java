package com.example.inventory_service.controller;

import com.example.inventory_service.dto.StockDecreaseDTO;
import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.dto.StockResponseDTO;
import com.example.inventory_service.entity.Stock;
import com.example.inventory_service.mapper.StockMapper;
import com.example.inventory_service.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public StockResponseDTO createOrUpdate(@Valid @RequestBody StockRequestDTO dto) {
        return StockMapper.toResponse(stockService.createOrUpdateStock(dto));
    }
    @GetMapping("/quantity/{platId}")
    public Integer getQuantityByPlatId(@PathVariable Long platId) {
        Stock stock = stockService.getByPlatId(platId);
        return stock.getQuantity();
    }

    @GetMapping("/{platId}")
    public StockResponseDTO getByPlatId(@PathVariable Long platId) {
        return StockMapper.toResponse(stockService.getByPlatId(platId));
    }
    // ✅ GET juste la disponibilité (boolean)
    @GetMapping("/available/{platId}")
    public Boolean isAvailable(@PathVariable Long platId) {
        Stock stock = stockService.getByPlatId(platId);
        return stock.getAvailable(); // ou getAvailable() selon votre entité
    }

    // ✅ APPELÉ PAR ORDER SERVICE VIA FEIGN
    @PutMapping("/decrease")
    public void decreaseStock(@Valid @RequestBody StockDecreaseDTO dto) {
        stockService.decreaseStock(dto.getPlatId(), dto.getQuantity());
    }
}


