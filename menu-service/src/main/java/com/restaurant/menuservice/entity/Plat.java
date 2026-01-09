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

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private double prix;

    @Column(nullable = false)
    private boolean disponible;
    @Column(name = "image_url")
    private String imageUrl; // Juste ajouter ce champ

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
}

