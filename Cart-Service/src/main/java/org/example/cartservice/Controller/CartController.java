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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Gestion des Paniers", description = "API pour gérer les paniers (session et utilisateur)")
public class CartController {

    private final CartService cartService;

    // ========== 1. ENDPOINTS SESSION (sans authentification) ==========

    @Operation(
            summary = "Créer un panier pour session anonyme",
            description = "Crée un nouveau panier pour un utilisateur non connecté avec un ID de session"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Panier créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Session ID manquant dans les headers"
            )
    })
    @PostMapping("/session")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponseDTO createCartForSession(
            @Parameter(description = "ID de session (doit être dans le header X-Session-Id)", required = true)
            @RequestHeader("X-Session-Id") String sessionId) {
        CartRequestDTO request = new CartRequestDTO();
        request.setSessionId(sessionId);
        return cartService.createCart(request);
    }

    @Operation(
            summary = "Récupérer un panier par session ID",
            description = "Retourne le panier associé à une session anonyme"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier non trouvé pour cette session"
            )
    })
    @GetMapping("/session/{sessionId}")
    public CartResponseDTO getCartBySession(
            @Parameter(description = "ID de session", required = true, example = "sess_123456789")
            @PathVariable String sessionId) {
        return cartService.getCartBySessionId(sessionId);
    }

    @Operation(
            summary = "Ajouter un item au panier de session",
            description = "Ajoute un plat au panier d'un utilisateur non connecté"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item ajouté avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier non trouvé ou plat non disponible"
            )
    })
    @PostMapping("/session/{sessionId}/items")
    public CartResponseDTO addItemToSessionCart(
            @Parameter(description = "ID de session", required = true)
            @PathVariable String sessionId,
            @Parameter(description = "Détails de l'item à ajouter", required = true)
            @Valid @RequestBody CartItemRequestDTO request) {
        Long cartId = cartService.findCartIdBySessionId(sessionId);
        return cartService.addItemToCart(cartId, request);
    }

    // ========== 2. ENDPOINTS UTILISATEUR (avec JWT) ==========

    @Operation(
            summary = "Créer un panier pour utilisateur connecté",
            description = "Crée un nouveau panier pour un utilisateur authentifié",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Panier utilisateur créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponseDTO createCartForUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        CartRequestDTO request = new CartRequestDTO();
        request.setCustomerId(userId);
        return cartService.createCart(request);
    }

    @Operation(
            summary = "Récupérer le panier de l'utilisateur connecté",
            description = "Retourne le panier de l'utilisateur authentifié",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier utilisateur trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier non trouvé pour cet utilisateur"
            )
    })
    @GetMapping("/user")
    public CartResponseDTO getCartByUser(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return cartService.getCartByCustomerId(userId);
    }

    @Operation(
            summary = "Ajouter un item au panier utilisateur",
            description = "Ajoute un plat au panier de l'utilisateur connecté",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item ajouté au panier utilisateur",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @PostMapping("/user/items")
    public CartResponseDTO addItemToUserCart(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "Détails de l'item à ajouter", required = true)
            @Valid @RequestBody CartItemRequestDTO request) {
        String userId = jwt.getSubject();
        Long cartId = cartService.findCartIdByCustomerId(userId);
        return cartService.addItemToCart(cartId, request);
    }

    // ========== 3. ENDPOINTS COMMUNS (par cartId) ==========

    @Operation(
            summary = "Créer un panier (générique)",
            description = "Crée un nouveau panier avec sessionId ou customerId"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Panier créé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartResponseDTO createCart(
            @Parameter(description = "Détails du panier à créer", required = true)
            @Valid @RequestBody CartRequestDTO request) {
        return cartService.createCart(request);
    }

    @Operation(
            summary = "Récupérer un panier par son ID",
            description = "Retourne un panier spécifique par son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier trouvé",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier non trouvé"
            )
    })
    @GetMapping("/{cartId}")
    public CartResponseDTO getCart(
            @Parameter(description = "ID du panier", required = true, example = "1")
            @PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    @Operation(
            summary = "Ajouter un item à un panier spécifique",
            description = "Ajoute un plat à un panier existant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Item ajouté au panier",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier non trouvé ou plat non disponible"
            )
    })
    @PostMapping("/{cartId}/items")
    public CartResponseDTO addItemToCart(
            @Parameter(description = "ID du panier", required = true)
            @PathVariable Long cartId,
            @Parameter(description = "Détails de l'item à ajouter", required = true)
            @Valid @RequestBody CartItemRequestDTO request) {
        return cartService.addItemToCart(cartId, request);
    }

    @Operation(
            summary = "Supprimer un item du panier",
            description = "Retire un item spécifique d'un panier"
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
    @DeleteMapping("/{cartId}/items/{itemId}")
    public CartResponseDTO removeItem(
            @Parameter(description = "ID du panier", required = true)
            @PathVariable Long cartId,
            @Parameter(description = "ID de l'item à supprimer", required = true)
            @PathVariable Long itemId) {
        return cartService.removeItem(cartId, itemId);
    }

    // ========== 4. FUSION DE PANIERS ==========

    @Operation(
            summary = "Fusionner les paniers",
            description = "Fusionne le panier de session avec le panier utilisateur lors de la connexion",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Paniers fusionnés avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Panier session non trouvé"
            )
    })
    @PostMapping("/merge")
    public CartResponseDTO mergeCarts(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,
            @Parameter(description = "ID de session à fusionner (header X-Session-Id)", required = true)
            @RequestHeader("X-Session-Id") String sessionId) {
        String userId = jwt.getSubject();
        return cartService.mergeCarts(sessionId, userId);
    }

    // ========== 5. ENDPOINTS SUPPLÉMENTAIRES UTILES ==========

    @Operation(
            summary = "Vider le panier",
            description = "Supprime tous les items d'un panier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Panier vidé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @DeleteMapping("/{cartId}/clear")
    public CartResponseDTO clearCart(
            @Parameter(description = "ID du panier", required = true)
            @PathVariable Long cartId) {
        return cartService.clearCart(cartId);
    }

    @Operation(
            summary = "Mettre à jour la quantité d'un item",
            description = "Modifie la quantité d'un item spécifique dans le panier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantité mise à jour",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CartResponseDTO.class))
            )
    })
    @PutMapping("/{cartId}/items/{itemId}")
    public CartResponseDTO updateItemQuantity(
            @Parameter(description = "ID du panier", required = true)
            @PathVariable Long cartId,
            @Parameter(description = "ID de l'item", required = true)
            @PathVariable Long itemId,
            @Parameter(description = "Nouvelle quantité", required = true)
            @RequestParam Integer quantity) {
        return cartService.updateItemQuantity(cartId, itemId, quantity);
    }

    @Operation(
            summary = "Supprimer un panier",
            description = "Supprime complètement un panier"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Panier supprimé avec succès"
            )
    })
    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(
            @Parameter(description = "ID du panier", required = true)
            @PathVariable Long cartId) {
        cartService.deleteCart(cartId);
    }
}