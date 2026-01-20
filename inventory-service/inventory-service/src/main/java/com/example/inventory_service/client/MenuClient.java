package com.example.inventory_service.client;

import com.example.inventory_service.dto.PlatResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "menu-service",
        url = "http://localhost:1800"
)
public interface MenuClient {

    @GetMapping("/api/plats/{id}")
    PlatResponseDTO getPlatById(@PathVariable Long id);
}


