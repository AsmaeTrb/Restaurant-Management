package org.example.cartservice.Exception;

public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException(String message) {
        super(message);
    }

    public DishNotFoundException(Long dishId) {
        super("Plat non trouvé avec ID: " + dishId);
    }
}