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

    @NotNull(message = "Customer ID est obligatoire")
    private Long customerId;

    @NotEmpty(message = "La commande doit contenir au moins un plat")
    private List<OrderItemDTO> items;

    @NotNull(message = "Le statut de la commande est obligatoire")
    private OrderStatus status; // PENDING, CONFIRMED, CANCELLED
}

