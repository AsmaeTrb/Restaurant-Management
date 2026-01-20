package com.restaurant.orderservice.kafka;
import com.restaurant.orderservice.dto.PaymentResponse;
import com.restaurant.orderservice.client.StockClient;
import com.restaurant.orderservice.dto.PaymentResultStatus;
import com.restaurant.orderservice.dto.StockDecreaseRequest;
import com.restaurant.orderservice.entity.Order;
import com.restaurant.orderservice.entity.OrderItem;
import com.restaurant.orderservice.entity.OrderStatus;
import com.restaurant.orderservice.kafka.PaymentResultEvent;
import com.restaurant.orderservice.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PaymentResultConsumer {

    private final OrderRepository orderRepository;
    private final StockClient stockClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentResultConsumer(OrderRepository orderRepository,
                                 StockClient stockClient) {
        this.orderRepository = orderRepository;
        this.stockClient = stockClient;
    }

    @KafkaListener(topics = "payment-result-topic", groupId = "order-service")
    public void consume(PaymentResultEvent event) {

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->
                        new RuntimeException("Order not found " + event.getOrderId())
                );

        // ❌ Paiement échoué
        if (event.getStatus() == PaymentResultStatus.FAILED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            return;
        }

        // ✅ Paiement confirmé
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        // ✅ Décrémenter le stock
        List<OrderItem> items = order.getItemsJson()
                .stream()
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, OrderItem.class);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        for (OrderItem item : items) {
            stockClient.decreaseStock(
                    new StockDecreaseRequest(
                            item.getPlatId(),
                            item.getQuantity()
                    )
            );
        }
    }
}
