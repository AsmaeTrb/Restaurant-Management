package org.example.cartservice.FeignClient;
import org.example.cartservice.DTO.DishInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "menu-service",
        url = "${menu.service.url:http://localhost:8010}"
)
public interface MenuServiceFeignClient {

    @GetMapping("/api/plats/{platId}")
    DishInfoDTO getDishInfo(@PathVariable Long platId);
}
