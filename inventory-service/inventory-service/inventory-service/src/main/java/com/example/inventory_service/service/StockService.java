package com.example.inventory_service.service;

import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.entity.Stock;

public interface StockService {

    Stock createOrUpdateStock(StockRequestDTO dto);

    Stock getByPlatId(Long platId);

    void decreaseStock(Long platId, Integer quantity);
}





