package com.example.inventory_service.controller;

import com.example.inventory_service.dto.StockDecreaseDTO;
import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.dto.StockResponseDTO;
import com.example.inventory_service.mapper.StockMapper;
import com.example.inventory_service.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping
    public StockResponseDTO createOrUpdate(@Valid @RequestBody StockRequestDTO dto) {
        return StockMapper.toResponse(stockService.createOrUpdateStock(dto));
    }

    @GetMapping("/quantity/{platId}")
    public ResponseEntity<Integer> getQuantity(@PathVariable Long platId) {
        Integer quantity = stockService.getQuantityByPlatId(platId);
        return ResponseEntity.ok(quantity);
    }

    @GetMapping("/available/{platId}")
    public ResponseEntity<Boolean> isAvailable(@PathVariable Long platId) {
        Boolean available = stockService.isAvailable(platId);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/{platId}")
    public ResponseEntity<StockResponseDTO> getStockByPlatId(@PathVariable Long platId) {
        StockResponseDTO response = stockService.getStockResponseByPlatId(platId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/decrease")
    public void decreaseStock(@Valid @RequestBody StockDecreaseDTO dto) {
        stockService.decreaseStock(dto.getPlatId(), dto.getQuantity());
    }
}