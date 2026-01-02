package com.restaurant.menuservice.repository;
import com.restaurant.menuservice.entity.Plat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatRepository extends JpaRepository<Plat, Long> {
    // Ici tu peux ajouter des requêtes custom si besoin
    // Ex: List<Plat> findByDisponibleTrue();
}

