package com.restaurant.orderservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.orderservice.dto.*;
import com.restaurant.orderservice.entity.Order;
import com.restaurant.orderservice.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ================= DTO → ENTITY =================
    public static Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setItemsJson(convertItemsToJson(dto.getItems()));
        order.setTotal(calculateTotal(dto.getItems()));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentId(null);
        return order;
    }

    // ================= CART → ENTITY =================
    public static Order cartToEntity(List<CartItemResponseDTO> cartItems, Long customerId) {
        Order order = new Order();
        order.setId(UUID.randomUUID().toString());
        order.setCustomerId(customerId);
        order.setItemsJson(convertCartItemsToJson(cartItems));
        order.setTotal(calculateTotalFromCart(cartItems));
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentId(null);
        return order;
    }

    // ================= ENTITY → DTO =================
    public static OrderResponseDTO toResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getCustomerId(),
                convertJsonToItems(order.getItemsJson()),
                order.getTotal(),
                order.getOrderDate(),
                order.getStatus(),
                order.getPaymentId()
        );
    }

    // ================= HELPERS =================

    public static List<String> convertItemsToJson(List<OrderItemDTO> items) {
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

    private static List<String> convertCartItemsToJson(List<CartItemResponseDTO> cartItems) {
        // Convertir CartItemResponseDTO → OrderItemDTO
        List<OrderItemDTO> orderItems = cartItems.stream()
                .map(cartItem -> {
                    OrderItemDTO orderItem = new OrderItemDTO();
                    orderItem.setPlatId(cartItem.getPlatId());
                    orderItem.setPrice(cartItem.getUnitPrice());
                    orderItem.setQuantity(cartItem.getQuantity());
                    return orderItem;
                })
                .toList();

        return convertItemsToJson(orderItems);
    }

    public static List<OrderItemDTO> convertJsonToItems(List<String> itemsJson) {
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

    private static double calculateTotal(List<OrderItemDTO> items) {
        return items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    private static double calculateTotalFromCart(List<CartItemResponseDTO> cartItems) {
        return cartItems.stream()
                .mapToDouble(CartItemResponseDTO::getSubtotal)
                .sum();
    }
}