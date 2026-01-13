package com.restaurant.orderservice.service;

import com.restaurant.orderservice.dto.OrderCreatedEvent;
import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.entity.Order;
import com.restaurant.orderservice.exception.ResourceNotFoundException;
import com.restaurant.orderservice.kafka.OrderEventProducer;
import com.restaurant.orderservice.mapper.OrderMapper;
import com.restaurant.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderEventProducer orderEventProducer
    ) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {

        // 1 Créer et sauvegarder la commande
        Order order = OrderMapper.toEntity(request);
        Order savedOrder = orderRepository.save(order);

        // 2 Créer l’événement Kafka
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(savedOrder.getId())
                .amount(savedOrder.getTotal())
                .currency("MAD")
                .build();

        // 3Publier l’événement
        orderEventProducer.publishOrderCreated(event);

        // 4 Retourner la réponse
        return OrderMapper.toResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found with id : " + id)
                );
        return OrderMapper.toResponseDTO(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(String id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Order not found with id : " + id);
        }
        orderRepository.deleteById(id);
    }
}




