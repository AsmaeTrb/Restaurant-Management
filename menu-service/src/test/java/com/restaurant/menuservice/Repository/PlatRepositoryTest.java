package com.restaurant.menuservice.Repository;

import com.restaurant.menuservice.entity.Plat;
import com.restaurant.menuservice.repository.PlatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class PlatRepositoryTest {

    @Autowired
    private PlatRepository platRepository;

    private Plat plat;

    @BeforeEach
    void setUp() {
        plat = new Plat();
        plat.setNom("Pizza");
        plat.setPrix(12.5);
        plat.setDisponible(true);
        plat.setImageUrl("pizza.jpg");

        platRepository.save(plat);
    }

    @Test
    void save_plat_success() {
        assertThat(plat.getId()).isNotNull();
    }

    @Test
    void findById_success() {
        Optional<Plat> result = platRepository.findById(plat.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getNom()).isEqualTo("Pizza");
    }

    @Test
    void findAll_success() {
        assertThat(platRepository.findAll()).isNotEmpty();
    }

    @Test
    void delete_plat_success() {
        platRepository.delete(plat);

        Optional<Plat> result = platRepository.findById(plat.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void count_plats() {
        long count = platRepository.count();
        assertThat(count).isGreaterThan(0);
    }
}
