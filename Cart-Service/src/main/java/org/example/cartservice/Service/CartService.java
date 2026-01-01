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
public class CartService {

    private final CartRepository cartRepository;
    private final MenuServiceFeignClient menuFeignClient;  // ← DIRECT !
    private final CartMapper cartMapper;
    public CartService(CartRepository cartRepository,MenuServiceFeignClient menuFeignClient,CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.menuFeignClient = menuFeignClient;
        this.cartMapper = cartMapper;
    }

    // ========== AJOUTER UN PLAT AU PANIER ==========

    public CartResponseDTO addItemToCart(Long cartId, CartItemRequestDTO request) {
        // 1. Trouver le panier
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));

        // 2. APPEL DIRECT AU MENU SERVICE VIA FEIGN
        DishInfoDTO dishInfo;
        try {
            dishInfo = menuFeignClient.getDishInfo(request.getPlatId());
            log.info("✅ Plat récupéré depuis Menu Service: {} ({}€)",
                    dishInfo.getNom(), dishInfo.getPrix());
        } catch (Exception e) {
            log.error("❌ Erreur avec Menu Service pour plat {}", request.getPlatId(), e);
            throw new DishNotFoundException(request.getPlatId());
        }

        // 3. Vérifier la disponibilité
        if (dishInfo == null) {
            throw new DishNotFoundException(request.getPlatId());
        }

        if (!dishInfo.isDisponible()) {
            throw new DishUnavailableException(dishInfo.getNom(), dishInfo.getId());
        }

        // 4. Créer CartItem avec les données
        CartItem newItem = new CartItem();
        newItem.setPlatId(dishInfo.getId());
        newItem.setDishName(dishInfo.getNom());
        newItem.setUnitPrice(dishInfo.getPrix());
        newItem.setQuantity(request.getQuantity());
        newItem.setAvailable(dishInfo.isDisponible());

        // 5. Ajouter au panier
        cart.addItem(newItem);

        // 6. Sauvegarder
        Cart savedCart = cartRepository.save(cart);
        log.info("🛒 Plat ajouté au panier {}: {} x {}",
                cartId, request.getQuantity(), dishInfo.getNom());

        return cartMapper.toResponseDTO(savedCart);
    }

    // ========== AUTRES MÉTHODES ==========

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
}