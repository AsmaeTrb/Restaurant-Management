package com.restaurant.menuservice.service;

import com.restaurant.menuservice.dto.PlatRequestDTO;
import com.restaurant.menuservice.dto.PlatResponseDTO;
import com.restaurant.menuservice.entity.Categorie;
import com.restaurant.menuservice.entity.Plat;
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

    public PlatService(PlatRepository platRepository, CategorieRepository categorieRepository) {
        this.platRepository = platRepository;
        this.categorieRepository = categorieRepository;
    }

    // Créer un plat
    public PlatResponseDTO createPlat(PlatRequestDTO dto) {
        Plat plat = PlatMapper.toEntity(dto);

        // Récupérer la catégorie depuis l'ID
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Categorie non trouvée"));
        plat.setCategorie(categorie);

        Plat saved = platRepository.save(plat);
        return PlatMapper.toResponse(saved);
    }

    // Lister tous les plats
    public List<PlatResponseDTO> getAllPlats() {
        return platRepository.findAll().stream()
                .map(PlatMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Modifier un plat
    public PlatResponseDTO updatePlat(Long id, PlatRequestDTO dto) {
        Plat plat = platRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plat non trouvé"));
        plat.setNom(dto.getNom());
        plat.setPrix(dto.getPrix());
        plat.setDisponible(dto.isDisponible());

        // Mettre à jour la catégorie
        Categorie categorie = categorieRepository.findById(dto.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Categorie non trouvée"));
        plat.setCategorie(categorie);

        Plat updated = platRepository.save(plat);
        return PlatMapper.toResponse(updated);
    }

    // Supprimer un plat
    public void deletePlat(Long id) {
        platRepository.deleteById(id);
    }
}
