package org.example.cartservice.Controller;

import org.example.cartservice.DTO.*;
import org.example.cartservice.Service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Gestion des Paniers", description = "API pour gérer les paniers des utilisateurs connectés")
public class CartController {
    private final CartService cartService;
    @Operation(
            summary = "Récupérer le panier de l'utilisateur",
            description = "Retourne le panier de l'utilisateur authentifié",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier non trouvé pour cet utilisateur"
            )
    })
    @GetMapping
    public CartResponseDTO getCart(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return cartService.getCartByCustomerId(userId);
    }

    @Operation(
            summary = "Ajouter un item au panier",
            description = "Ajoute un plat au panier de l'utilisateur connecté",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item ajouté au panier",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @PostMapping("/items")
    public CartResponseDTO addItem(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CartItemRequestDTO request) {
        String userId = jwt.getSubject();
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.addItemToCart(cartId, request);
    }

    @Operation(
            summary = "Mettre à jour la quantité d'un item",
            description = "Modifie la quantité d'un item dans le panier",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantité mise à jour",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @PutMapping("/items/{itemId}")
    public CartResponseDTO updateItemQuantity(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID de l'item", required = true)
            @PathVariable Long itemId,
            @Parameter(description = "Nouvelle quantité", required = true)
            @RequestParam Integer quantity) {
        String userId = jwt.getSubject();
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.updateItemQuantity(cartId, itemId, quantity);
    }

    @Operation(
            summary = "Supprimer un item du panier",
            description = "Retire un item spécifique du panier",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item supprimé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier ou item non trouvé"
            )
    })
    @DeleteMapping("/items/{itemId}")
    public CartResponseDTO removeItem(
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID de l'item à supprimer", required = true)
            @PathVariable Long itemId) {
        String userId = jwt.getSubject();
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.removeItem(cartId, itemId);
    }

    @Operation(
            summary = "Vider le panier",
            description = "Supprime tous les items du panier",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier vidé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @DeleteMapping("/clear")
    public CartResponseDTO clearCart(
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.clearCart(cartId);
    }
    @Operation(
            summary = "Fusionner les paniers",
            description = "Fusionne le panier de session avec le panier utilisateur lors de la connexion",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/merge")
    public CartResponseDTO mergeCarts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID de session à fusionner (header X-Session-Id)", required = true)
            @RequestHeader("X-Session-Id") String sessionId) {
        String userId = jwt.getSubject();
        return cartService.mergeCarts(sessionId, userId);
    }

}