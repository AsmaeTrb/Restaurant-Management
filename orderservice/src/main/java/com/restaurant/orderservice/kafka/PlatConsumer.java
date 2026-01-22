package com.restaurant.orderservice.kafka;

import com.restaurant.orderservice.dto.PlatDTO;
import com.restaurant.orderservice.service.OrderServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PlatConsumer {

    private final OrderServiceImpl orderService;

    public PlatConsumer(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }


    // Quand un plat est créé ou modifié

    @KafkaListener(topics = "menu-topic", groupId = "order-group")
    public void consumePlatEvent(PlatDTO plat) {
        System.out.println("Reçu plat event: " + plat);
        // TODO : mettre à jour les commandes si nécessaire
        // ex : vérifier si une commande contient ce plat et ajuster le prix / disponibilité
    }

    // Quand un plat est supprimé

    @KafkaListener(topics = "plat-deleted-topic", groupId = "order-group")
    public void consumePlatDeletedEvent(String platId) {
        System.out.println("Plat supprimé : " + platId);
        // TODO : marquer les commandes contenant ce plat comme "à réviser" ou "invalide"
    }
}

