package com.restaurant.menuservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "stock-service",
        url = "http://localhost:1700"
)
public interface StockClient {
    // ✅ create/update stock
    @PostMapping("/stocks")
    StockResponse createOrUpdate(@RequestBody StockRequest dto);

    // Vérifier la quantité en stock pour un plat
    @GetMapping("/stocks/quantity/{platId}")
    Integer getStockQuantity(@PathVariable("platId") Long platId);

    // Vérifier la disponibilité (si available = true)
    @GetMapping("/stocks/available/{platId}")
    Boolean isAvailable(@PathVariable("platId") Long platId);
    record StockResponse(
            Long id,
            Long platId,
            Integer quantity,
            Boolean available
    ) {}
    record StockRequest(Long platId, Integer quantity) {}
}