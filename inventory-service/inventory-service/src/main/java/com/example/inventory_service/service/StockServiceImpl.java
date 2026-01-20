package com.example.inventory_service.service;

import com.example.inventory_service.client.MenuClient;
import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.entity.Stock;
import com.example.inventory_service.exception.InsufficientStockException;
import com.example.inventory_service.exception.InvalidQuantityException;
import com.example.inventory_service.exception.StockNotFoundException;
import com.example.inventory_service.mapper.StockMapper;
import com.example.inventory_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final MenuClient menuClient;

    // ===============================
    // CREATE / UPDATE STOCK
    // ===============================
    @Override
    public Stock createOrUpdateStock(StockRequestDTO dto) {

        try {
            menuClient.getPlatById(dto.getPlatId());
        } catch (Exception e) {
            //  On transforme l'erreur Feign en exception métier STOCK
            throw new StockNotFoundException(
                    "Plat not found or Menu service unavailable for id " + dto.getPlatId()
            );
        }

        Stock stock = stockRepository
                .findByPlatId(dto.getPlatId())
                .orElse(StockMapper.toEntity(dto));

        stock.setQuantity(dto.getQuantity());
        stock.setAvailable(dto.getQuantity() > 0);

        return stockRepository.save(stock);
    }


    // ===============================
    // GET STOCK
    // ===============================
    @Override
    public Stock getByPlatId(Long platId) {
        return stockRepository.findByPlatId(platId)
                .orElseThrow(() ->
                        new StockNotFoundException("Stock not found for platId " + platId));
    }

    // ===============================
    // DECREASE STOCK (appelé par Order)
    // ===============================
    @Override
    public void decreaseStock(Long platId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than 0");
        }

        Stock stock = getByPlatId(platId);

        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for platId " + platId +
                            " | available=" + stock.getQuantity() +
                            " | requested=" + quantity
            );
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setAvailable(stock.getQuantity() > 0);

        stockRepository.save(stock);
    }
}







