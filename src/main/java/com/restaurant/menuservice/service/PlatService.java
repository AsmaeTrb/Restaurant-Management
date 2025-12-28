package com.restaurant.menuservice.service;

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
    private final PlatProducer platProducer; // Kafka producer

    public PlatService(PlatRepository platRepository,
                       CategorieRepository categorieRepository,
                       PlatProducer platProducer) {
        this.platRepository = platRepository;
        this.categorieRepository = categorieRepository;
        this.platProducer = platProducer;
    }

    // Créer un plat
    public PlatResponseDTO createPlat(PlatRequestDTO dto) {
        Plat plat = PlatMapper.toEntity(dto);

        // Récupérer la catégorie depuis l'ID avec exception personnalisée
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        plat.setCategorie(categorie);

        Plat saved = platRepository.save(plat);
        PlatResponseDTO response = PlatMapper.toResponse(saved);

        // Publier l'événement Kafka
        platProducer.sendPlatEvent(response);

        return response;
    }

    // Lister tous les plats
    public List<PlatResponseDTO> getAllPlats() {
        return platRepository.findAll().stream()
                .map(PlatMapper::toResponse)
                .collect(Collectors.toList());
    }
    public PlatResponseDTO getPlatById(Long id) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));
        return PlatMapper.toResponse(plat);
    }

    // Modifier un plat
    public PlatResponseDTO updatePlat(Long id, PlatRequestDTO dto) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plat non trouvé"));
        plat.setNom(dto.getNom());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());

        // Mettre à jour la catégorie
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        plat.setCategorie(categorie);

        Plat updated = platRepository.save(plat);
        PlatResponseDTO response = PlatMapper.toResponse(updated);

        // Publier l'événement Kafka après update
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

