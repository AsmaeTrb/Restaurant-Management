package org.example.userservice.Repository;
import org.example.userservice.Entity.Role;
import org.example.userservice.Entity.User;
import org.example.userservice.Configuration.RsaKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    // 🔐 IMPORTANT : mock de RsaKeys pour éviter les erreurs de sécurité
    @MockitoBean
    private RsaKeys rsaKeys;

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


    // ==========================
    // TEST findByEmail
    // ==========================
    @Test
    void findUserByEmail_success() {
        // GIVEN
        String email = "test@mail.com";

        // WHEN
        Optional<User> result = userRepository.findByEmail(email);

        // THEN
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);
    }

    @Test
    void findUserByEmail_notFound() {
        // GIVEN
        String email = "unknown@mail.com";

        // WHEN
        Optional<User> result = userRepository.findByEmail(email);

        // THEN
        assertThat(result).isEmpty();
    }

    // ==========================
    // TEST existsByEmail
    // ==========================
    @Test
    void existsByEmail_true() {
        // WHEN
        Boolean exists = userRepository.existsByEmail("test@mail.com");

        // THEN
        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_false() {
        // WHEN
        Boolean exists = userRepository.existsByEmail("fake@mail.com");

        // THEN
        assertThat(exists).isFalse();
    }
}
