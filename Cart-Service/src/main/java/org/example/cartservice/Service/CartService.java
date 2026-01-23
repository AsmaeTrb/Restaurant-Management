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

        // ✅ STOCK CHECK (si ton Menu-Service renvoie stockQuantity / stockAvailable)
        Integer stockQty = dishInfo.getStockQuantity();
        Boolean stockAvailable = dishInfo.getStockAvailable();

        if (Boolean.FALSE.equals(stockAvailable) || stockQty == null || stockQty <= 0) {
            throw new IllegalArgumentException("Stock insuffisant pour ce plat");
        }

        // quantité déjà dans le panier ?
        CartItem existing = cart.getItems().stream()
                .filter(i -> i.getPlatId().equals(request.getPlatId()))
                .findFirst()
                .orElse(null);

        int currentQty = existing != null ? existing.getQuantity() : 0;
        int requestedQty = request.getQuantity();
        int finalQty = currentQty + requestedQty;

        if (finalQty > stockQty) {
            throw new IllegalArgumentException(
                    "Stock insuffisant. Stock disponible: " + stockQty + ", dans panier: " + currentQty
            );
        }

        // ✅ UTILISATION DU MAPPER
        CartItem newItem = cartMapper.toCartItem(request, dishInfo);

        // relation + logique métier
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

        // ✅ récupérer stock depuis menu-service
        DishInfoDTO dishInfo;
        try {
            dishInfo = menuFeignClient.getDishInfo(item.getPlatId());
        } catch (Exception e) {
            throw new DishNotFoundException(item.getPlatId());
        }

        Integer stockQty = dishInfo.getStockQuantity();
        Boolean stockAvailable = dishInfo.getStockAvailable();

        if (Boolean.FALSE.equals(stockAvailable) || stockQty == null || stockQty <= 0) {
            throw new IllegalArgumentException("Stock insuffisant pour ce plat");
        }

        if (quantity > stockQty) {
            throw new IllegalArgumentException(
                    "Quantité demandée (" + quantity + ") dépasse le stock (" + stockQty + ")"
            );
        }

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

            // 1) récupérer stock depuis menu-service
            DishInfoDTO dishInfo;
            try {
                dishInfo = menuFeignClient.getDishInfo(sessionItem.getPlatId());
            } catch (Exception e) {
                throw new DishNotFoundException(sessionItem.getPlatId());
            }

            Integer stockQty = dishInfo.getStockQuantity();
            Boolean stockAvailable = dishInfo.getStockAvailable();

            if (Boolean.FALSE.equals(stockAvailable) || stockQty == null || stockQty <= 0) {
                throw new IllegalArgumentException("Stock insuffisant pour platId=" + sessionItem.getPlatId());
            }

            // 2) trouver item existant dans user cart
            CartItem existing = userCart.getItems().stream()
                    .filter(i -> i.getPlatId() != null && i.getPlatId().equals(sessionItem.getPlatId()))
                    .findFirst()
                    .orElse(null);

            int currentQty = existing != null ? existing.getQuantity() : 0;
            int finalQty = currentQty + sessionItem.getQuantity();

            if (finalQty > stockQty) {
                throw new IllegalArgumentException(
                        "Stock insuffisant pour platId=" + sessionItem.getPlatId() +
                                " stock=" + stockQty + " demandé=" + finalQty
                );
            }

            if (existing != null) {
                existing.setQuantity(finalQty);
                existing.setUnitPrice(sessionItem.getUnitPrice());
                existing.setDishName(sessionItem.getDishName());
                existing.setAvailable(sessionItem.isAvailable());
                existing.setImageUrl(sessionItem.getImageUrl());
            } else {
                sessionItem.setCart(userCart);
                userCart.getItems().add(sessionItem); // ou recréer un new item si tu veux
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