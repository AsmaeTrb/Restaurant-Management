package com.example.inventory_service.mapper;

import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.dto.StockResponseDTO;
import com.example.inventory_service.entity.Stock;

public class StockMapper {

    public static Stock toEntity(StockRequestDTO dto) {
        return Stock.builder()
                .platId(dto.getPlatId())
                .quantity(dto.getQuantity())
                .available(dto.getQuantity() > 0)
                .build();
    }

    public static StockResponseDTO toResponse(Stock stock) {
        return StockResponseDTO.builder()
                .id(stock.getId())
                .platId(stock.getPlatId())
                .quantity(stock.getQuantity())
                .available(stock.getAvailable())
                .build();
    }
}



