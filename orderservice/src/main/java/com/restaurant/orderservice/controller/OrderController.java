package com.restaurant.orderservice.controller;

import com.restaurant.orderservice.dto.CheckoutRequestDTO;
import com.restaurant.orderservice.dto.ConfirmPaymentRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.service.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl service;

    // ✅ SEUL endpoint de création (depuis panier)

    @PostMapping
    public OrderResponseDTO checkout(@AuthenticationPrincipal Jwt jwt,
                                     @RequestHeader("Authorization") String authorization,
                                     @Valid @RequestBody CheckoutRequestDTO req) {
        return service.createOrder(jwt, authorization, req);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDTO getById(@AuthenticationPrincipal Jwt jwt,
                                    @PathVariable String orderId) {
        return service.getById(jwt, orderId);
    }
    @PutMapping("/{orderId}/payment")
    public OrderResponseDTO confirmPayment(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Authorization") String authorization,
            @PathVariable String orderId,
            @Valid @RequestBody ConfirmPaymentRequestDTO req
    ) {
        return service.confirmPayment(jwt,orderId, authorization, req);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAll(
            @AuthenticationPrincipal Jwt jwt
    ) {
        return ResponseEntity.ok(service.getAll());
    }
}