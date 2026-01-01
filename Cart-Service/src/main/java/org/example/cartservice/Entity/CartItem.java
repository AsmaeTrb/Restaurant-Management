package org.example.cartservice.Entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "plat_id", nullable = false)
    private Long platId;           // ID du plat (référence)
    @Column(name = "dish_name", nullable = false)
    private String dishName;    // Nom du plat (ex: "Pizza")

    @Column(name = "unit_price", nullable = false)
    private double unitPrice;   // Prix unitaire (ex: 12.50)

    @Column(nullable = false)
    private int quantity = 1;   // Quantité (ex: 2 pizzas)
    @Column(name = "available", nullable = false)
    private boolean available = true;  // Copie de la disponibilit

    @ManyToOne  // Relation : plusieurs CartItem → 1 Cart
    @JoinColumn(name = "cart_id")  // Colonne qui référence le panier
    private Cart cart;          // Le panier auquel appartient cet article

    // Calculer le sous-total
    public double getSubtotal() {
        return unitPrice * quantity;
    }
    public void increaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity += amount;
        }
    }

    // Diminuer la quantité de CET article
    public void decreaseQuantity(int amount) {
        if (amount > 0) {
            this.quantity = Math.max(1, this.quantity - amount);
        }
    }
}