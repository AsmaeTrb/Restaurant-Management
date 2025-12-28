package com.restaurant.menuservice.repository;
import com.restaurant.menuservice.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    // Ici on peut ajouter des méthodes personnalisées si besoin
    // Ex: Optional<Categorie> findByNom(String nom);


}

