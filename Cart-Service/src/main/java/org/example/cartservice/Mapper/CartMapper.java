package org.example.cartservice.Mapper;

import org.example.cartservice.DTO.*;
import org.example.cartservice.Entity.Cart;
import org.example.cartservice.Entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CartMapper {


    public CartResponseDTO toResponseDTO(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponseDTO dto = new CartResponseDTO();
        dto.setId(cart.getId());
        dto.setSessionId(cart.getSessionId());
        dto.setCustomerId(cart.getCustomerId());
        dto.setActive(cart.isActive());
        dto.setTotal(cart.getTotal());

        // Convertir les items
        if (cart.getItems() != null) {
            dto.setItems(
                    cart.getItems().stream()
                            .map(this::toCartItemResponseDTO)
                            .collect(Collectors.toList())
            );
        }

        // Calculer totalItems
        dto.setTotalItems(cart.getTotalItems());

        return dto;
    }

    // ========== CART ITEM → CART ITEM RESPONSE DTO ==========
    public CartItemResponseDTO toCartItemResponseDTO(CartItem item) {
        if (item == null) {
            return null;
        }

        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(item.getId());
        dto.setPlatId(item.getPlatId());
        dto.setDishName(item.getDishName());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setQuantity(item.getQuantity());
        dto.setAvailable(item.isAvailable());
        dto.setSubtotal(item.getSubtotal());
        dto.setImageUrl(item.getImageUrl()); // ← AJOUTEZ CETTE LIGNE


        return dto;
    }

    // ========== DISH INFO + QUANTITY → CART ITEM ==========

    public CartItem toCartItem(DishInfoDTO dishInfo, int quantity) {
        if (dishInfo == null) {
            return null;
        }

        CartItem item = new CartItem();
        item.setPlatId(dishInfo.getId());
        item.setDishName(dishInfo.getNom());
        item.setUnitPrice(dishInfo.getPrix());
        item.setQuantity(quantity);
        item.setAvailable(dishInfo.isDisponible());
        item.setImageUrl(dishInfo.getImageUrl()); // ← AJOUTEZ CETTE LIGNE

        // Note: cart sera défini plus tard dans le service

        return item;
    }

    // ========== CART ITEM REQUEST + DISH INFO → CART ITEM ==========

    public CartItem toCartItem(CartItemRequestDTO request, DishInfoDTO dishInfo) {
        if (request == null || dishInfo == null) {
            return null;
        }

        return toCartItem(dishInfo, request.getQuantity());
    }
}