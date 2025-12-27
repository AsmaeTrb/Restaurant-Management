package org.example.userservice.Mapper;

import org.example.userservice.DTO.UserRequestDto;
import org.example.userservice.DTO.UserResponseDto;
import org.example.userservice.Entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // DTO -> Entity
    public User toEntity(UserRequestDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        // ❌ password et role = service
        return user;
    }

    // Entity -> Response DTO
    public UserResponseDto toResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole().name());
        return dto;
    }
}
