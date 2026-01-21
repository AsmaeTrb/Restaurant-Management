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

}