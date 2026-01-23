package org.example.userservice.Repository;

import org.example.userservice.Configuration.RsaKeys;
import org.example.userservice.Entity.Role;
import org.example.userservice.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @MockBean
    private RsaKeys rsaKeys; // ✅ مهم باش مايتحمّلاش Security config

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
        u.setRole(Role.CLIENT); // بدلها إلا عندك CLIENT/USER...

        userRepository.save(u);
    }

    // ==========================
    // TEST findByEmail
    // ==========================
    @Test
    void findUserByEmail_success() {
        String email = "test@mail.com";

        Optional<User> result = userRepository.findByEmail(email);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    void findUserByEmail_notFound() {
        Optional<User> result = userRepository.findByEmail("unknown@mail.com");

        assertThat(result).isEmpty();
    }

    // ==========================
    // TEST existsByEmail
    // ==========================
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
