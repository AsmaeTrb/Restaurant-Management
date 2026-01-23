package com.restaurant.orderservice.service;

import com.restaurant.orderservice.client.CartClient;
import com.restaurant.orderservice.client.PaymentClient;
import com.restaurant.orderservice.client.StockClient;
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
    private final CartClient cartClient;
    private final StockClient stockClient; // AJOUT// déjà déclaré

    public OrderServiceImpl(OrderRepository repo,
                            PaymentClient paymentClient,
                            CartClient cartClient,
                            StockClient stockClient) { // AJOUT ICI
        this.repo = repo;
        this.paymentClient = paymentClient;
        this.cartClient = cartClient; // AJOUT ICI
        this.stockClient = stockClient;
    }

    public OrderResponseDTO createOrder(Jwt jwt, String authorization, CheckoutRequestDTO req) {
        Long userId = jwt.getClaim("userId");

        CartResponseDTO cart = cartClient.getMyCart(authorization);

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Panier vide");
        }

        // ✅ créer order avec pickup
        Order order = OrderMapper.cartToEntity(cart.getItems(), userId, req.getPickupDate(), req.getPickupSlot());
        Order saved = repo.save(order);

        // ✅ créer paiement
        CreatePaymentRequestDTO payReq = new CreatePaymentRequestDTO();
        payReq.setOrderId(saved.getId());
        payReq.setAmount(saved.getTotal());
        payReq.setCurrency("MAD");
        payReq.setPaymentMethod(PaymentMethod.CARD);

        PaymentResponse payRes = paymentClient.createPayment(authorization, payReq);

        saved.setPaymentId(payRes.getPaymentId());
        repo.save(saved);

        // ✅ vider panier
        cartClient.clear(authorization);

        return OrderMapper.toResponseDTO(saved);
    }

    public OrderResponseDTO confirmPayment(Jwt jwt, String orderId, String authorization, ConfirmPaymentRequestDTO req) {

        Long userId = jwt.getClaim("userId");

        Order order = repo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        // ✅ Sécurité : l'utilisateur ne confirme que sa commande
        if (order.getCustomerId() == null || !order.getCustomerId().equals(userId)) {
            throw new RuntimeException("Not authorized to confirm this order");
        }

        // ✅ éviter double confirmation (et double décrémentation stock)
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return OrderMapper.toResponseDTO(order);
        }

        PaymentResponse payRes = paymentClient.confirmPayment(authorization, order.getPaymentId(), req);

        if (payRes.getStatus() == PaymentStatus.COMPLETED) {
            order.setStatus(OrderStatus.CONFIRMED);

            // décrémenter stock
            try {
                List<OrderItemDTO> items = OrderMapper.convertJsonToItems(order.getItemsJson());
                for (OrderItemDTO item : items) {
                    stockClient.decreaseStock(
                            authorization,
                            new StockDecreaseRequest(item.getPlatId(), item.getQuantity())
                    );
                }
            } catch (Exception e) {
                // ✅ au choix : soit annuler, soit logger (mais logger = risque d'incohérence)
                order.setStatus(OrderStatus.CANCELLED);
                repo.save(order);
                throw new RuntimeException("Erreur décrémentation stock. Commande annulée.", e);
            }

        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        return OrderMapper.toResponseDTO(repo.save(order));
    }
    public OrderResponseDTO getById(Jwt jwt, String orderId) {
        Long userId = jwt.getClaim("userId");

        Order order = repo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        // ✅ sécurité : user ne voit que sa commande
        if (order.getCustomerId() == null || !order.getCustomerId().equals(userId)) {
            throw new RuntimeException("Not authorized to access this order");
        }

        return OrderMapper.toResponseDTO(order);
    }


    public List<OrderResponseDTO> getAll() {
        return repo.findAll()
                .stream()
                .map(OrderMapper::toResponseDTO)
                .toList();
    }
}
