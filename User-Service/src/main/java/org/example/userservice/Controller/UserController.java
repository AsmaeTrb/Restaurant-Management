package org.example.userservice.Controller;

import org.example.userservice.DTO.UserRequestDto;
import org.example.userservice.DTO.UserResponseDto;
import org.example.userservice.Services.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public UserResponseDto getProfile(Authentication authentication) {
        return userService.getMyProfile(authentication.getName());
    }

    @PutMapping("/profile")
    public UserResponseDto updateProfile(Authentication authentication,
                                           @RequestBody UserRequestDto userRequestDto) {
        return userService.updateMyProfile(
                authentication.getName(),
                userRequestDto.getPhone(),
                userRequestDto.getAddress()
        );
    }
}
