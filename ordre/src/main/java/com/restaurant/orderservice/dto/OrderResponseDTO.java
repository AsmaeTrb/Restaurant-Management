package com.restaurant.orderservice.dto;
import com.restaurant.orderservice.entity.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {

    private String id;
    private Long customerId;
    private List<OrderItemDTO> items;
    private double total;
    private LocalDateTime orderDate;
    private OrderStatus status;
}

