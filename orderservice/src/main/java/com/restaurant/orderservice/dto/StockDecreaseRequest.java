package com.restaurant.orderservice.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDecreaseRequest {
    private Long platId;
    private Integer quantity;
}

