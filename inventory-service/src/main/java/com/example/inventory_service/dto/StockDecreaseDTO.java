package com.example.inventory_service.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockDecreaseDTO {

    @NotNull
    private Long platId;

    @NotNull
    @Min(1)
    private Integer quantity;
    public Long getPlatId() {
        return platId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}

//Cette classe sert quand Order  Stock pour décrémenter après paiement.
