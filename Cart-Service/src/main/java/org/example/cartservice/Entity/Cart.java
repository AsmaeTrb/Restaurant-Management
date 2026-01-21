package org.example.cartservice.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    private boolean active = true;
    private double total = 0.0;

    // Relation : 1 Panier → plusieurs CartItem
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    public void addItem(CartItem newItem) {
        if (newItem == null || newItem.getPlatId() == null) {
            throw new IllegalArgumentException("CartItem ou platId invalide");
        }

        int qtyToAdd = Math.max(1, newItem.getQuantity());

        Optional<CartItem> existing = items.stream()
                .filter(i -> i.getPlatId().equals(newItem.getPlatId()))
                .findFirst();

        if (existing.isPresent()) {
            // même plat → on additionne
            existing.get().increaseQuantity(qtyToAdd);
        } else {
            // nouveau plat → nouvelle ligne
            newItem.setQuantity(qtyToAdd);
            newItem.setCart(this);
            items.add(newItem);
        }

        calculateTotal();
    }


    /** Supprime un item par son id */
    public void removeItem(Long itemId) {
        if (itemId == null) return;

        items.removeIf(item -> {
            boolean match = itemId.equals(item.getId());
            if (match) item.setCart(null); // propre avec orphanRemoval
            return match;
        });

        calculateTotal();
    }

    public void clear() {
        items.forEach(i -> i.setCart(null));
        items.clear();
        total = 0.0;
    }

    /** Total = somme des sous-totaux (plus cohérent métier) */
    public void calculateTotal() {
        this.total = items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}