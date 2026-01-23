package com.restaurant.orderservice.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequestDTO {

    @NotNull
    private LocalDate pickupDate;

    @NotBlank
    private String pickupSlot;
}
