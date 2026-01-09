package com.restaurant.menuservice.controller;
import com.restaurant.menuservice.dto.CategorieRequestDTO;
import com.restaurant.menuservice.dto.CategorieResponseDTO;
import com.restaurant.menuservice.service.CategorieService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @PostMapping
    public CategorieResponseDTO createCategorie(@RequestBody CategorieRequestDTO dto) {
        return categorieService.createCategorie(dto);
    }

    @GetMapping
    public List<CategorieResponseDTO> getAllCategories() {
        return categorieService.getAllCategories();
    }

    @PutMapping("/{id}")
    public CategorieResponseDTO updateCategorie(@PathVariable Long id, @RequestBody CategorieRequestDTO dto) {
        return categorieService.updateCategorie(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
    }
}

