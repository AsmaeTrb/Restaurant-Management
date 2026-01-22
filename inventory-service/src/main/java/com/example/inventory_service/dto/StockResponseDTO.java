package com.example.inventory_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockResponseDTO {

    private Long id;
    private Long platId;
    private Integer quantity;
    private Boolean available;
}


