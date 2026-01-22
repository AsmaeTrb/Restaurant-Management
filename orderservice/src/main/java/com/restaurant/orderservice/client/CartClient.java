package com.restaurant.orderservice.client;

import com.restaurant.orderservice.dto.CartResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="cart-service", url="http://localhost:8084")
public interface CartClient {

    @GetMapping("/api/carts")
    CartResponseDTO getMyCart(@RequestHeader("Authorization") String authorization);

    @DeleteMapping("/api/carts/clear")
    CartResponseDTO clear(@RequestHeader("Authorization") String authorization);
}

