package org.example.cartservice.Exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(Long cartId) {
        super("Panier non trouvé avec ID: " + cartId);
    }
}