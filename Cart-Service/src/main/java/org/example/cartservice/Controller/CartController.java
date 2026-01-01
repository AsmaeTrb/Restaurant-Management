package org.example.cartservice.Controller;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.License;
import org.example.cartservice.DTO.CartItemRequestDTO;
import org.example.cartservice.DTO.CartRequestDTO;
import org.example.cartservice.DTO.CartResponseDTO;
import org.example.cartservice.Service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@OpenAPIDefinition(
        info = @Info(
                title = "Cart Service API",
                version = "1.0",
                description = "API de gestion du panier (Restaurant)",
                contact = @Contact(
                        name = "Cart Team",
                        email = "cart@restaurant.com"
                ),
                license = @License(
                        name = "Apache 2.0"
                )
        )
)
public class CartController {

    private final   CartService cartService;
    public CartController (CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponseDTO createCart(
            @Valid @RequestBody CartRequestDTO request) {
        return cartService.createCart(request);
    }

    @GetMapping("/{cartId}")
    public CartResponseDTO getCart(@PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/{cartId}/items")
    public CartResponseDTO addItemToCart(
            @PathVariable Long cartId,
            @Valid @RequestBody CartItemRequestDTO request) {
        return cartService.addItemToCart(cartId, request);
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public CartResponseDTO removeItem(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        return cartService.removeItem(cartId, itemId);
    }

    @DeleteMapping("/{cartId}/clear")
    public CartResponseDTO clearCart(@PathVariable Long cartId) {
        // À implémenter plus tard
        return cartService.getCart(cartId);
    }
}