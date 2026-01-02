package org.example.cartservice.Exception;

public class CartItemNotFoundException extends RuntimeException {

    public CartItemNotFoundException(Long itemId) {
        super("Item du panier non trouvé avec ID: " + itemId);}}