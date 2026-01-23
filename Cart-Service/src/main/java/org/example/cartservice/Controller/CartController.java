package org.example.cartservice.Controller;

import jakarta.validation.Valid;
import org.example.cartservice.DTO.*;
import org.example.cartservice.Service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;
public CartController(CartService cartService) {
    this.cartService = cartService;
}
    // Méthode utilitaire pour DEBUG
    private String extractUserId(Jwt jwt) {
        System.out.println("=== JWT DEBUG ===");

        // 1. Afficher TOUS les claims
        System.out.println("All JWT claims:");
        jwt.getClaims().forEach((key, value) -> {
            System.out.println("  " + key + " = " + value + " (type: " +
                    (value != null ? value.getClass().getSimpleName() : "null") + ")");
        });

        // 2. Essayer différents claims
        String userId = null;

        // Essayer "userId"
        Object userIdClaim = jwt.getClaim("userId");
        System.out.println("Claim 'userId': " + userIdClaim);

        if (userIdClaim != null) {
            userId = userIdClaim.toString();
            System.out.println("✅ Using userId from claim: " + userId);
        }
        // Essayer "sub" (subject)
        else if (jwt.getSubject() != null) {
            userId = jwt.getSubject();
            System.out.println("⚠️ Using subject as userId: " + userId);
        }
        // Essayer "email"
        else {
            Object emailClaim = jwt.getClaim("email");
            if (emailClaim != null) {
                userId = emailClaim.toString();
                System.out.println("⚠️ Using email as userId: " + userId);
            } else {
                System.err.println("❌ ERROR: No userId found in JWT!");
                userId = "unknown";
            }
        }

        System.out.println("Final userId: " + userId);
        return userId;
    }

    @GetMapping
    public CartResponseDTO getCart(@AuthenticationPrincipal Jwt jwt) {
        String userId = extractUserId(jwt);
        return cartService.getCartByCustomerId(userId);
    }

    @PostMapping("/items")
    public CartResponseDTO addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CartItemRequestDTO request) {

        System.out.println("=== ADD ITEM ===");
        System.out.println("Request: platId=" + request.getPlatId() + ", quantity=" + request.getQuantity());

        String userId = extractUserId(jwt);
        Long cartId = cartService.findCartIdByCustomerId(userId);

        System.out.println("Cart ID: " + cartId);
        System.out.println("Calling addItemToCart...");

        return cartService.addItemToCart(cartId, request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponseDTO updateItemQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        String userId = extractUserId(jwt);
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.updateItemQuantity(cartId, itemId, quantity);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponseDTO removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long itemId) {
        String userId = extractUserId(jwt);
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.removeItem(cartId, itemId);
    }

    @DeleteMapping("/clear")
    public CartResponseDTO clearCart(@AuthenticationPrincipal Jwt jwt) {
        String userId = extractUserId(jwt);
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.clearCart(cartId);
    }

    @PostMapping("/merge")
    public CartResponseDTO mergeCarts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("X-Session-Id") String sessionId) {

        System.out.println("=== MERGE CART DEBUG ===");

        // 1. Vérifier le header
        System.out.println("Session ID from header: " + sessionId);
        if (sessionId == null || sessionId.isEmpty()) {
            System.err.println("❌ ERROR: X-Session-Id header is missing or empty!");
            throw new IllegalArgumentException("X-Session-Id header is required");
        }

        // 2. Vérifier le JWT
        System.out.println("JWT Subject: " + jwt.getSubject());
        System.out.println("All JWT claims:");
        jwt.getClaims().forEach((k, v) -> System.out.println("  " + k + " = " + v));

        // 3. Utiliser la MÊME méthode pour extraire l'userId
        String userId = extractUserId(jwt); // ← CHANGER ICI
        System.out.println("Using userId: " + userId);

        // 4. Appeler le service
        try {
            System.out.println("Calling cartService.mergeCarts(" + sessionId + ", " + userId + ")");
            CartResponseDTO result = cartService.mergeCarts(sessionId, userId);
            System.out.println("✅ Merge successful!");
            System.out.println("Result cart ID: " + result.getId());
            System.out.println("Result items: " + result.getItems().size());
            return result;
        } catch (Exception e) {
            System.err.println("❌ ERROR in mergeCarts: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}