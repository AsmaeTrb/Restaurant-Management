package com.restaurant.orderservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.orderservice.dto.OrderItemDTO;
import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.entity.Order;
import com.restaurant.orderservice.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ================= DTO → ENTITY =================
    public static Order toEntity(OrderRequestDTO dto) {
        return new Order(
                UUID.randomUUID().toString(),
                dto.getCustomerId(),
                convertItemsToJson(dto.getItems()),
                calculateTotal(dto.getItems()),
                LocalDateTime.now(),
                OrderStatus.PENDING //  la commande commence TOUJOURS en PENDING
        );
    }



    // ================= ENTITY → DTO =================
    public static OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                convertJsonToItems(order.getItemsJson()),
                order.getTotal(),
                order.getOrderDate(),
                order.getStatus() // ✅ DIRECT
        );
    }


    // ================= HELPERS =================

    private static List<String> convertItemsToJson(List<OrderItemDTO> items) {
        return items.stream()
                .map(item -> {
                    try {
                        return objectMapper.writeValueAsString(item);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erreur JSON OrderItemDTO", e);
                    }
                })
                .toList();
    }

    private static List<OrderItemDTO> convertJsonToItems(List<String> itemsJson) {
        return itemsJson.stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, OrderItemDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Erreur JSON → OrderItemDTO", e);
                    }
                })
                .toList();
    }

    // Le client ne calcule JAMAIS le total
    private static double calculateTotal(List<OrderItemDTO> items) {
        return items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }
}


