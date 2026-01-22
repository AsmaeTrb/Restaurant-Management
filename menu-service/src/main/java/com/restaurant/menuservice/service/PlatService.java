package com.restaurant.menuservice.service;

import com.restaurant.menuservice.client.StockClient;
import com.restaurant.menuservice.dto.PlatRequestDTO;
import com.restaurant.menuservice.dto.PlatResponseDTO;
import com.restaurant.menuservice.entity.Categorie;
import com.restaurant.menuservice.entity.Plat;
import com.restaurant.menuservice.exception.ResourceNotFoundException;
import com.restaurant.menuservice.kafka.PlatProducer;
import com.restaurant.menuservice.mapper.PlatMapper;
import com.restaurant.menuservice.repository.CategorieRepository;
import com.restaurant.menuservice.repository.PlatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlatService {

    private final PlatRepository platRepository;
    private final CategorieRepository categorieRepository;
    private final PlatProducer platProducer;
    private final StockClient stockClient;// Kafka producer

    public PlatService(PlatRepository platRepository,
                       CategorieRepository categorieRepository,
                       PlatProducer platProducer,StockClient stockClient) {
        this.platRepository = platRepository;
        this.categorieRepository = categorieRepository;
        this.platProducer = platProducer;
        this.stockClient = stockClient;
    }

    public PlatResponseDTO createPlat(PlatRequestDTO dto) {
        Plat plat = PlatMapper.toEntity(dto);

        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        plat.setCategorie(categorie);

        // 1) save plat => obtenir platId
        Plat saved = platRepository.save(plat);

        // 2) créer stock initial en parallèle (inventory-service)
        Integer qtyInit = dto.getInitialQuantity() != null ? dto.getInitialQuantity() : 0;

        try {
            stockClient.createOrUpdate(new StockClient.StockRequest(saved.getId(), qtyInit));
        } catch (Exception e) {
            // ✅ Important: compensation simple (sinon tu auras un plat sans stock)
            platRepository.deleteById(saved.getId());
            throw new RuntimeException("Erreur création stock (inventory-service). Plat annulé.", e);
        }

        // 3) récupérer infos stock pour la réponse
        Integer qty = stockClient.getStockQuantity(saved.getId());
        Boolean available = stockClient.isAvailable(saved.getId());

        PlatResponseDTO response = PlatMapper.toResponse(saved, qty, available);

        platProducer.sendPlatEvent(response);
        return response;
    }


    // Lister tous les plats
    public List<PlatResponseDTO> getAllPlats() {
        return platRepository.findAll().stream()
                .map(plat -> {
                    Integer qty = stockClient.getStockQuantity(plat.getId());
                    Boolean available = stockClient.isAvailable(plat.getId());
                    return PlatMapper.toResponse(plat, qty, available);
                })
                .collect(Collectors.toList());
    }
    public PlatResponseDTO getPlatById(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));
        Integer qty = stockClient.getStockQuantity(plat.getId());
        Boolean available = stockClient.isAvailable(plat.getId());

        return PlatMapper.toResponse(plat, qty, available);

    }

    // Modifier un plat
    public PlatResponseDTO updatePlat(Long id, PlatRequestDTO dto) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));

        plat.setNom(dto.getNom());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());
        plat.setImageUrl(dto.getImageUrl());

        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        plat.setCategorie(categorie);

        Plat updated = platRepository.save(plat);

        Integer qty = stockClient.getStockQuantity(plat.getId());
        Boolean available = stockClient.isAvailable(plat.getId());

        // Utiliser la méthode AVEC stock !
        PlatResponseDTO response = PlatMapper.toResponse(updated, qty, available);

        platProducer.sendPlatEvent(response);
        return response;
    }
    // Supprimer un plat
    public void deletePlat(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));
        platRepository.delete(plat);

        // Publier un événement “Plat supprimé”
        platProducer.sendPlatDeletedEvent(id);
    }
}

