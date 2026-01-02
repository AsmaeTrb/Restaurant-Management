package com.restaurant.menuservice.service;

import com.restaurant.menuservice.dto.CategorieRequestDTO;
import com.restaurant.menuservice.dto.CategorieResponseDTO;
import com.restaurant.menuservice.entity.Categorie;
import com.restaurant.menuservice.exception.ResourceNotFoundException;
import com.restaurant.menuservice.mapper.CategorieMapper;
import com.restaurant.menuservice.repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public CategorieService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    // Créer une catégorie
    public CategorieResponseDTO createCategorie(CategorieRequestDTO dto) {
        Categorie categorie = CategorieMapper.toEntity(dto);
        Categorie saved = categorieRepository.save(categorie);
        return CategorieMapper.toResponse(saved);
    }

    // Lister toutes les catégories
    public List<CategorieResponseDTO> getAllCategories() {
        return categorieRepository.findAll().stream()
                .map(CategorieMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Modifier une catégorie
    public CategorieResponseDTO updateCategorie(Long id, CategorieRequestDTO dto) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        categorie.setNom(dto.getNom());
        Categorie updated = categorieRepository.save(categorie);
        return CategorieMapper.toResponse(updated);
    }

    // Supprimer une catégorie
    public void deleteCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie non trouvée"));
        categorieRepository.delete(categorie);
    }
}


