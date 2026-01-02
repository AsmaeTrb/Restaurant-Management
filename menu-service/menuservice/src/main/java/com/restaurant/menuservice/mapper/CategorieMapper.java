package com.restaurant.menuservice.mapper;
import com.restaurant.menuservice.dto.CategorieRequestDTO;
import com.restaurant.menuservice.dto.CategorieResponseDTO;
import com.restaurant.menuservice.entity.Categorie;

public class CategorieMapper {

    // RequestDTO -> Entity
    public static Categorie toEntity(CategorieRequestDTO dto) {
        Categorie categorie = new Categorie();
        categorie.setNom(dto.getNom());
        return categorie;
    }

    // Entity -> ResponseDTO
    public static CategorieResponseDTO toResponse(Categorie categorie) {
        return new CategorieResponseDTO(
                categorie.getId(),
                categorie.getNom()
        );
    }
}

