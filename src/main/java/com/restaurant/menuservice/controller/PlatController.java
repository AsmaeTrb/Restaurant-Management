package com.restaurant.menuservice.controller;
import com.restaurant.menuservice.dto.PlatRequestDTO;
import com.restaurant.menuservice.dto.PlatResponseDTO;
import com.restaurant.menuservice.service.PlatService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plats")
public class PlatController {

    private final PlatService platService;

    public PlatController(PlatService platService) {
        this.platService = platService;
    }

    @PostMapping
    public PlatResponseDTO createPlat(@RequestBody PlatRequestDTO dto) {
        return platService.createPlat(dto);
    }

    @GetMapping
    public List<PlatResponseDTO> getAllPlats() {
        return platService.getAllPlats();
    }

    @PutMapping("/{id}")
    public PlatResponseDTO updatePlat(@PathVariable Long id, @RequestBody PlatRequestDTO dto) {
        return platService.updatePlat(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deletePlat(@PathVariable Long id) {
        platService.deletePlat(id);
    }
}

