package com.restaurant.orderservice.client;



import com.restaurant.orderservice.dto.StockDecreaseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", url = "http://localhost:1700")
public interface StockClient {

    @PostMapping("/stocks/decrease")
    void decreaseStock(@RequestBody StockDecreaseRequest request);
}

