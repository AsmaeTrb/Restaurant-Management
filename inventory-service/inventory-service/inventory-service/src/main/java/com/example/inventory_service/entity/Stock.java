package com.example.inventory_service.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks",
        uniqueConstraints = @UniqueConstraint(columnNames = "platId"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long platId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Boolean available;
}




