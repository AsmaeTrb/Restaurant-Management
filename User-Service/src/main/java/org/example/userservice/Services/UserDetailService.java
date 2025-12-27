package org.example.userservice.Services;

import org.example.userservice.Entity.User;
import org.example.userservice.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;
    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Authorities (roles)
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + u.getRole().name()));

        // Return Spring Security user
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),        // username used by Spring Security
                u.getPassword(),     // hashed password
                authorities
        );
    }


}
