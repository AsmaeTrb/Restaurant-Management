package com.restaurant.orderservice.controller;

import com.restaurant.orderservice.dto.OrderRequestDTO;
import com.restaurant.orderservice.dto.OrderResponseDTO;
import com.restaurant.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    // CREATE ORDER

    @Operation(summary = "Créer une nouvelle commande")
    @ApiResponse(responseCode = "201", description = "Commande créée avec succès")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(
            @Valid @RequestBody OrderRequestDTO request
    ) {
        OrderResponseDTO response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // GET ORDER BY ID
    @Operation(summary = "Récupérer une commande par son ID")
    @ApiResponse(responseCode = "200", description = "Commande récupérée avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }


    // GET ALL ORDERS

    @Operation(summary = "Récupérer toutes les commandes")
    @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    // DELETE ORDER

    @Operation(summary = "Supprimer une commande par son ID")
    @ApiResponse(responseCode = "204", description = "Commande supprimée avec succès")
    @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

//why l'utilisation de ce @valid
//
//Active les validations définies dans OrderRequestDTO
//
// commande vide , customerId null
//
//Spring renvoie automatiquement une erreur 400