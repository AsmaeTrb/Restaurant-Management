package org.example.cartservice.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDTO {

    @NotNull(message = "L'ID du plat est obligatoire")
    private Long platId;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    private int quantity = 1;
}