package com.restaurant.menuservice.kafka;

import com.restaurant.menuservice.dto.PlatResponseDTO;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlatProducer {

    private final KafkaTemplate<String, PlatResponseDTO> kafkaTemplate;
    private final KafkaTemplate<String, String> kafkaTemplateString; // pour l'événement suppression

    private final String TOPIC = "menu-topic";                  // pour create/update
    private final String TOPIC_PLAT_DELETED = "plat-deleted-topic"; // pour delete

    // Injecter deux KafkaTemplate différents
    public PlatProducer(KafkaTemplate<String, PlatResponseDTO> kafkaTemplate,
                        KafkaTemplate<String, String> kafkaTemplateString) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateString = kafkaTemplateString;
    }

    // Plat créé ou modifié (DTO complet)
    public void sendPlatEvent(PlatResponseDTO plat) {
        kafkaTemplate.send(TOPIC, plat);
    }

    // Plat supprimé (on envoie juste l'ID)
    public void sendPlatDeletedEvent(Long platId) {
        kafkaTemplateString.send(TOPIC_PLAT_DELETED, platId.toString());
    }
}


