package org.example.cartservice.Service;

import org.example.cartservice.DTO.*;
import org.example.cartservice.Entity.*;
import org.example.cartservice.Exception.*;
import org.example.cartservice.FeignClient.MenuServiceFeignClient;
import org.example.cartservice.Mapper.CartMapper;
import org.example.cartservice.Repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MenuServiceFeignClient menuFeignClient;
    private final CartMapper cartMapper;


    public CartResponseDTO getCartByCustomerId(String customerId) {

        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    newCart.setActive(true);
                    newCart.setTotal(0.0);
                    return cartRepository.save(newCart);
                });

        return cartMapper.toResponseDTO(cart);
    }

    public Long findCartIdByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .map(Cart::getId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(customerId);
                    newCart.setActive(true);
                    newCart.setTotal(0.0);
                    return cartRepository.save(newCart).getId();
                });
    }


    // ========== MÉTHODE DE FUSION ==========

    // ========== MÉTHODES EXISTANTES (gardées telles quelles) ==========
    public CartResponseDTO addItemToCart(Long cartId, CartItemRequestDTO request) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));

        DishInfoDTO dishInfo;
        try {
            dishInfo = menuFeignClient.getDishInfo(request.getPlatId());
        } catch (Exception e) {
            throw new DishNotFoundException(request.getPlatId());
        }

        if (!dishInfo.isDisponible()) {
            throw new DishUnavailableException(dishInfo.getNom(), dishInfo.getId());
        }

        // ✅ UTILISATION DU MAPPER
        CartItem newItem = cartMapper.toCartItem(request, dishInfo);

        // la relation + logique métier
        cart.addItem(newItem);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(savedCart);
    }


    public CartResponseDTO createCart(CartRequestDTO request) {
        Cart cart = new Cart();
        cart.setCustomerId(request.getCustomerId());
        cart.setActive(true);

        Cart savedCart = cartRepository.save(cart);
        log.info("🆕 Panier créé: {}", savedCart.getId());

        return cartMapper.toResponseDTO(savedCart);
    }

    public CartResponseDTO getCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        return cartMapper.toResponseDTO(cart);
    }

    public CartResponseDTO removeItem(Long cartId, Long itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));

        cart.removeItem(itemId);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponseDTO(savedCart);
    }
    public CartResponseDTO clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));

        cart.clear();
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.toResponseDTO(savedCart);
    }
    public CartResponseDTO updateItemQuantity(Long cartId, Long itemId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException(itemId));

        item.setQuantity(quantity);
        cart.calculateTotal();

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toResponseDTO(savedCart);
    }
    public void deleteCart(Long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException(cartId);
        }
        cartRepository.deleteById(cartId);
    }
    // Dans CartService.java - ajoutez cette méthode
    @Transactional
    public CartResponseDTO mergeCarts(String sessionId, String userId) {

        // 1. Chercher le panier de session (guest)
        Cart sessionCart = cartRepository.findBySessionId(sessionId).orElse(null);

        // 2. Chercher ou créer le panier utilisateur
        Cart userCart = cartRepository.findByCustomerId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(userId);
                    newCart.setActive(true);
                    newCart.setTotal(0.0);
                    return cartRepository.save(newCart);
                });

        // 3. Si pas de panier session, retourner le panier user
        if (sessionCart == null || sessionCart.getItems() == null || sessionCart.getItems().isEmpty()) {
            return cartMapper.toResponseDTO(userCart);
        }

        // 4. Initialiser la liste si null
        if (userCart.getItems() == null) {
            userCart.setItems(new ArrayList<>());
        }

        // 5. Fusionner les items
        for (CartItem sessionItem : sessionCart.getItems()) {
            // Chercher si le plat existe déjà dans le panier user
            CartItem existing = userCart.getItems().stream()
                    .filter(i -> i.getPlatId() != null && i.getPlatId().equals(sessionItem.getPlatId()))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                // Additionner les quantités
                existing.setQuantity(existing.getQuantity() + sessionItem.getQuantity());

                // Mettre à jour les infos
                existing.setUnitPrice(sessionItem.getUnitPrice());
                existing.setDishName(sessionItem.getDishName());
                existing.setAvailable(sessionItem.isAvailable());
                existing.setImageUrl(sessionItem.getImageUrl());
            } else {
                // Créer un nouvel item
                CartItem newItem = new CartItem();
                newItem.setPlatId(sessionItem.getPlatId());
                newItem.setDishName(sessionItem.getDishName());
                newItem.setUnitPrice(sessionItem.getUnitPrice());
                newItem.setQuantity(sessionItem.getQuantity());
                newItem.setAvailable(sessionItem.isAvailable());
                newItem.setImageUrl(sessionItem.getImageUrl());

                // Lier au panier
                newItem.setCart(userCart);
                userCart.getItems().add(newItem);
            }
        }

        // 6. Recalculer le total
        userCart.calculateTotal();

        // 7. Sauvegarder le panier user
        Cart savedCart = cartRepository.save(userCart);

        // 8. Supprimer le panier session
        cartRepository.delete(sessionCart);

        log.info("✅ Fusion réussie: session {} → user {}", sessionId, userId);
        return cartMapper.toResponseDTO(savedCart);
    }

}