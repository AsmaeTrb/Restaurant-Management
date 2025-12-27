package org.example.userservice.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshTokenRequestDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
