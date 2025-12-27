package org.example.userservice.Services;
import org.example.userservice.DTO.AuthResponseDto;
import org.example.userservice.DTO.LoginRequestDto;
import org.example.userservice.DTO.RefreshTokenRequestDto;
import org.example.userservice.DTO.UserRequestDto;
import org.example.userservice.Entity.Role;
import org.example.userservice.Entity.User;
import org.example.userservice.Mapper.UserMapper;
import org.example.userservice.Repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
        import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;



    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtEncoder jwtEncoder,
                       JwtDecoder jwtDecoder,
                       UserRepository userRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        Instant instant = Instant.now();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );

        String scope = authentication.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.joining(" "));

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .subject(authentication.getName()) // email
                .issuer("User_Service")
                .issuedAt(instant)
                .expiresAt(instant.plus(2, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        JwtClaimsSet refreshClaims = JwtClaimsSet.builder()
                .subject(authentication.getName())
                .issuer("User_Service")
                .issuedAt(instant)
                .expiresAt(instant.plus(5, ChronoUnit.MINUTES))
                .build();

        String refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(refreshClaims)).getTokenValue();

        return new AuthResponseDto(accessToken, refreshToken);
    }

    public AuthResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {

        if (refreshTokenRequestDto == null
                || refreshTokenRequestDto.getRefreshToken() == null
                || refreshTokenRequestDto.getRefreshToken().isBlank()) {
            throw new IllegalArgumentException("refresh token is empty");
        }
        String refreshToken = refreshTokenRequestDto.getRefreshToken();
        Jwt jwt = jwtDecoder.decode(refreshToken);


        String email = jwt.getSubject(); // subject=email

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Instant instant = Instant.now();

        String scope = userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.joining(" "));

        JwtClaimsSet accessClaims = JwtClaimsSet.builder()
                .subject(userDetails.getUsername()) // email
                .issuer("User_Service")
                .issuedAt(instant)
                .expiresAt(instant.plus(2, ChronoUnit.MINUTES))
                .claim("scope", scope)
                .build();

        String newAccessToken = jwtEncoder.encode(JwtEncoderParameters.from(accessClaims)).getTokenValue();

        return new AuthResponseDto(newAccessToken, refreshToken);
    }
    public AuthResponseDto register(UserRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userMapper.toEntity(dto);

        // 3️⃣ Business logic (SERVICE)
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.CLIENT);

        // 4️⃣ Save
        userRepository.save(user);

        LoginRequestDto loginDto =
                new LoginRequestDto(dto.getEmail(), dto.getPassword());

        return login(loginDto);
    }

}
