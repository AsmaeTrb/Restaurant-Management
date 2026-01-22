package com.example.inventory_service.service;

import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.dto.StockResponseDTO;
import com.example.inventory_service.entity.Stock;

public interface StockService {

    Stock createOrUpdateStock(StockRequestDTO dto);


    void decreaseStock(Long platId, Integer quantity);
    Boolean isAvailable(Long platId); // ← AJOUTER
    StockResponseDTO getStockResponseByPlatId(Long platId); // ← AJOUTER
    Integer getQuantityByPlatId(Long platId); // ← AJOUTER CETTE MÉTHODE

}





