package com.restaurant.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatDTO {
    private Long id;
    private String nom;
    private double prix;
    private boolean disponible;
}

