//OrderItem n’est pas une table Cassandra séparée. Elle est imbriquée dans la colonne items de Order.
//Avec Cassandra, c’est la manière recommandée pour les relations “un-à-plusieurs” simples.

package com.restaurant.orderservice.entity;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    private Long platId;

    private int quantity;

    private double price; // prix unitaire du plat
}
