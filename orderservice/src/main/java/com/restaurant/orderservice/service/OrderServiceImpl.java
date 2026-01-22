package com.restaurant.orderservice.service;

import com.restaurant.orderservice.client.CartClient;
import com.restaurant.orderservice.client.PaymentClient;
import com.restaurant.orderservice.dto.*;
import com.restaurant.orderservice.entity.Order;
import com.restaurant.orderservice.entity.OrderStatus;
import com.restaurant.orderservice.enums.PaymentMethod;
import com.restaurant.orderservice.enums.PaymentStatus;
import com.restaurant.orderservice.exception.ResourceNotFoundException;
import com.restaurant.orderservice.mapper.OrderMapper;
import com.restaurant.orderservice.repository.OrderRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class OrderServiceImpl {

    private final OrderRepository repo;
    private final PaymentClient paymentClient;
    private final CartClient cartClient; // déjà déclaré

    public OrderServiceImpl(OrderRepository repo,
                            PaymentClient paymentClient,
                            CartClient cartClient) { // AJOUT ICI
        this.repo = repo;
        this.paymentClient = paymentClient;
        this.cartClient = cartClient; // AJOUT ICI
    }

    public OrderResponseDTO createOrder(Jwt jwt, String authorization) {
        Long userId = jwt.getClaim("userId");

        CartResponseDTO cart = cartClient.getMyCart(authorization); // ÇA VA MARCHER

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        Order order = OrderMapper.cartToEntity(cart.getItems(), userId);
        Order saved = repo.save(order);

        CreatePaymentRequestDTO payReq = new CreatePaymentRequestDTO();
        payReq.setOrderId(saved.getId());
        payReq.setAmount(saved.getTotal());
        payReq.setCurrency("MAD");
        payReq.setPaymentMethod(PaymentMethod.CARD);

        PaymentResponse payRes = paymentClient.createPayment(payReq);

        saved.setPaymentId(payRes.getPaymentId());
        repo.save(saved);

        cartClient.clear(authorization);

        return OrderMapper.toResponseDTO(saved);
    }


    public OrderResponseDTO confirmPayment(String orderId, ConfirmPaymentRequestDTO req) {
        Order order = repo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        PaymentResponse payRes =
                paymentClient.confirmPayment(order.getPaymentId(), req);

        if (payRes.getStatus() == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        return OrderMapper.toResponseDTO(repo.save(order));
    }

    public List<OrderResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(OrderMapper::toResponseDTO)
                .toList();
    }
}
