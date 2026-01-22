package com.example.inventory_service.service;

import com.example.inventory_service.dto.StockRequestDTO;
import com.example.inventory_service.dto.StockResponseDTO;
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

    @Override
    public Stock createOrUpdateStock(StockRequestDTO dto) {
        Stock stock = stockRepository
                .findByPlatId(dto.getPlatId())
                .orElse(StockMapper.toEntity(dto));

        stock.setQuantity(dto.getQuantity());
        stock.setAvailable(dto.getQuantity() > 0);

        return stockRepository.save(stock);
    }

    @Override
    public Integer getQuantityByPlatId(Long platId) {
        return stockRepository.findByPlatId(platId)
                .map(Stock::getQuantity)
                .orElse(0);
    }

    @Override
    public Boolean isAvailable(Long platId) {
        return stockRepository.findByPlatId(platId)
                .map(Stock::getAvailable)
                .orElse(false);
    }

    @Override
    public StockResponseDTO getStockResponseByPlatId(Long platId) {
        Stock stock = getByPlatId(platId);
        return StockMapper.toResponse(stock);
    }

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
    private Stock getByPlatId(Long platId) {
        return stockRepository.findByPlatId(platId)
                .orElseThrow(() -> new StockNotFoundException("Stock not found for platId: " + platId));
    }

}