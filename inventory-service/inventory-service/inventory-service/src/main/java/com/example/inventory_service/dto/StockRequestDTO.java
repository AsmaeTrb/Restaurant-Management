package com.example.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockRequestDTO {

    @NotNull
    private Long platId;

    @NotNull
    @Min(0)
    private Integer quantity;
}


