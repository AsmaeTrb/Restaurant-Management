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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final MenuServiceFeignClient menuFeignClient;
    private final CartMapper cartMapper;

    // ========== MÉTHODES SESSION ==========
    public CartResponseDTO getCartBySessionId(String sessionId) {
        Cart cart = cartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CartNotFoundException("Session: " + sessionId));
        return cartMapper.toResponseDTO(cart);
    }

    public Long findCartIdBySessionId(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
                .map(Cart::getId)
                .orElseThrow(() -> new CartNotFoundException("Session: " + sessionId));
    }

    // ========== MÉTHODES UTILISATEUR ==========
    public CartResponseDTO getCartByCustomerId(String customerId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartNotFoundException("User: " + customerId));
        return cartMapper.toResponseDTO(cart);
    }

    public Long findCartIdByCustomerId(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .map(Cart::getId)
                .orElseThrow(() -> new CartNotFoundException("User: " + customerId));
    }

    // ========== MÉTHODE DE FUSION ==========
    public CartResponseDTO mergeCarts(String sessionId, String userId) {

        // 1) Trouver le panier session
        Cart sessionCart = cartRepository.findBySessionId(sessionId).orElse(null);

        // 2) Trouver ou créer le panier utilisateur
        Cart userCart = cartRepository.findByCustomerId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomerId(userId);
                    newCart.setActive(true);
                    return cartRepository.save(newCart);
                });

        // 3) Fusion : additionner quantités si même platId
        if (sessionCart != null && sessionCart.getItems() != null && !sessionCart.getItems().isEmpty()) {

            for (CartItem sessionItem : sessionCart.getItems()) {

                // On crée un nouvel item à ajouter au panier user
                CartItem newItem = new CartItem();
                newItem.setPlatId(sessionItem.getPlatId());
                newItem.setDishName(sessionItem.getDishName());
                newItem.setUnitPrice(sessionItem.getUnitPrice());
                newItem.setQuantity(sessionItem.getQuantity());
                newItem.setAvailable(sessionItem.isAvailable());

                userCart.addItem(newItem);
            }

            // Option A (simple) : supprimer le panier session (comme tu fais déjà)
            cartRepository.delete(sessionCart);

            // Option B (plus safe) : le désactiver au lieu de delete
            // sessionCart.setActive(false);
            // sessionCart.clear();
            // cartRepository.save(sessionCart);
        }

        // 4) Sauvegarder le panier utilisateur (total déjà recalculé par addItem)
        Cart savedCart = cartRepository.save(userCart);

        log.info("✅ Paniers fusionnés (addition qty): session {} → user {}", sessionId, userId);
        return cartMapper.toResponseDTO(savedCart);
    }


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
        cart.setSessionId(request.getSessionId());
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

}