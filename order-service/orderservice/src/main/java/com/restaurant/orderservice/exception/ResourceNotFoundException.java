package com.restaurant.orderservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
//Utilisée quand :
//
//une commande n’existe pas
//
//un ID est invalide
