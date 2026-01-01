package org.example.cartservice.Exception;

public class DishUnavailableException extends RuntimeException {
    public DishUnavailableException(String message) {
        super(message);
    }

    public DishUnavailableException(String dishName, Long dishId) {
        super("Plat '" + dishName + "' (ID: " + dishId + ") n'est pas disponible");
    }
}