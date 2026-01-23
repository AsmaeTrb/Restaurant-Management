package org.example.cartservice.DTO;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDTO {
    private Long id;
    private Long platId;
    private String dishName;
    private double unitPrice;
    private int quantity;
    private boolean available;
    private double subtotal;
    private String imageUrl;
    private Integer stockQuantity;     // ✅
    private Boolean stockAvailable;
}