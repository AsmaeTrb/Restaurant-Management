package com.restaurant.menuservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double pourcentage;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "plat_id", nullable = false)
    private Plat plat;
}
