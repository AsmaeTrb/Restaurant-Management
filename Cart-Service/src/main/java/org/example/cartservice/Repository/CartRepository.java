
package org.example.cartservice.Repository;

import org.example.cartservice.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // Trouver un panier par sessionId
    Optional<Cart> findBySessionId(String sessionId);

    // Trouver un panier actif par sessionId
    Optional<Cart> findBySessionIdAndActiveTrue(String sessionId);

    // Trouver le panier actif d'un client
    Optional<Cart> findByCustomerIdAndActiveTrue(Long customerId);

    // Vérifier si un panier existe pour une session
    boolean existsBySessionId(String sessionId);

    // Trouver tous les paniers d'un client
    List<Cart> findByCustomerId(Long customerId);
}