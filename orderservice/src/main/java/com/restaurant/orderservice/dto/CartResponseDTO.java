package com.restaurant.orderservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private String customerId;
    private List<CartItemResponseDTO> items;
    private Double total;
    private boolean active;

}
