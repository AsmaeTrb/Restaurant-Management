package org.example.cartservice.DTO;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDTO {
    private Long id;
    private String sessionId;
    private Long customerId;
    private boolean active;
    private double total;
    private int totalItems;
    private List<CartItemResponseDTO> items;
}