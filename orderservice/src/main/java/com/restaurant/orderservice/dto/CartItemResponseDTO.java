package com.restaurant.orderservice.dto;

import lombok.Data;

@Data
public class CartItemResponseDTO {
    private Long platId;
    private String dishName;
    private Double unitPrice;
    private Integer quantity;
    private Double subtotal;
    private boolean available;
    private String imageUrl;
}
