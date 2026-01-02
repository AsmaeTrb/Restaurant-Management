package com.restaurant.orderservice.service;

import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO request);

    OrderResponseDTO getOrderById(String id);

    List<OrderResponseDTO> getAllOrders();

    void deleteOrder(String id);
}

