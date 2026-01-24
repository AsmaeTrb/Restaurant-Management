package com.restaurant.menuservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "plats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private double prix;

    private boolean disponible;
    private String imageUrl; // Juste ajouter ce champ

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
}

