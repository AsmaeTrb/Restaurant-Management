package com.restaurant.orderservice.dto;
import com.restaurant.orderservice.entity.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    @NotEmpty private List<OrderItemDTO> items;
}


