package org.example.userservice.Controller;

import jakarta.validation.Valid;
import org.example.userservice.DTO.AuthResponseDto;
import org.example.userservice.DTO.LoginRequestDto;
import org.example.userservice.DTO.RefreshTokenRequestDto;
import org.example.userservice.Services.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    public AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return authService.login(loginRequestDto);
    }
    @PostMapping("/refresh")
    public AuthResponseDto refresh(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return authService.refresh(refreshTokenRequestDto.getRefreshToken());
    }

}
