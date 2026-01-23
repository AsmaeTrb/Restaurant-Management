package org.example.userservice.Repository;

import org.example.userservice.Entity.Role;
import org.example.userservice.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setFirstName("Test");
        u.setLastName("User");
        u.setEmail("test@mail.com");
        u.setPassword("password");
        u.setPhone("0600000000");
        u.setAddress("Casablanca");
        u.setRole(Role.CLIENT);

        userRepository.save(u);
    }

    @Test
    void findUserByEmail_success() {
        Optional<User> result = userRepository.findByEmail("test@mail.com");
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void findUserByEmail_notFound() {
        Optional<User> result = userRepository.findByEmail("unknown@mail.com");
        assertThat(result).isEmpty();
    }

    @Test
    void existsByEmail_true() {
        Boolean exists = userRepository.existsByEmail("test@mail.com");
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_false() {
        Boolean exists = userRepository.existsByEmail("fake@mail.com");
        assertThat(exists).isFalse();
    }
}