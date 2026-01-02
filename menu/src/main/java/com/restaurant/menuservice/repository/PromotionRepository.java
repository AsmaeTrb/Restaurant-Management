package com.restaurant.menuservice.repository;

import com.restaurant.menuservice.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    // Ajouter des méthodes custom si nécessaire
    // Ex: List<Promotion> findByActiveTrue();
}

