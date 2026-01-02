package com.restaurant.menuservice.kafka;

import com.restaurant.menuservice.dto.PlatResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlatProducer {

    private final KafkaTemplate<String, PlatResponseDTO> kafkaTemplate;
    private final KafkaTemplate<String, String> kafkaTemplateString;

    private static final String TOPIC = "menu-topic";
    private static final String TOPIC_PLAT_DELETED = "plat-deleted-topic";

    public PlatProducer(
            @Qualifier("kafkaTemplatePlat") KafkaTemplate<String, PlatResponseDTO> kafkaTemplate,
            @Qualifier("kafkaTemplateString") KafkaTemplate<String, String> kafkaTemplateString) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTemplateString = kafkaTemplateString;
    }

    // Plat créé ou modifié
    public void sendPlatEvent(PlatResponseDTO plat) {
        kafkaTemplate.send(TOPIC, plat);
    }

    // Plat supprimé
    public void sendPlatDeletedEvent(Long platId) {
        kafkaTemplateString.send(TOPIC_PLAT_DELETED, platId.toString());
    }
}



