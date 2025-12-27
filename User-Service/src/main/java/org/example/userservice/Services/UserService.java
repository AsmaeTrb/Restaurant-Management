package org.example.userservice.Services;

import jakarta.transaction.Transactional;
import org.example.userservice.DTO.UserResponseDto;
import org.example.userservice.Entity.User;
import org.example.userservice.Mapper.UserMapper;
import org.example.userservice.Repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public UserService(UserRepository userRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    public UserResponseDto getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDto(user);
    }
    @Transactional
    public UserResponseDto updateMyProfile(String email, String phone, String address) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPhone(phone);
        user.setAddress(address);

        return userMapper.toResponseDto(user);
    }

}
