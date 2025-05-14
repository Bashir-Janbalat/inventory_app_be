package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.MovementType;
import org.inventory.app.mapper.StockMovementMapper;
import org.inventory.app.model.Product;
import org.inventory.app.projection.StockMovementProjection;
import org.inventory.app.repository.StockMovementRepository;
import org.inventory.app.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class StockServiceImpl implements StockService {

    private final StockMovementRepository stockMovementRepository;
    private final StockUpdater stockUpdater;

    @Override
    public void updateStocksFromDTO(Product product, ProductDTO dto) {
        stockUpdater.updateStocksFromDTO(product, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockMovementProjection> getStockMovements(Pageable pageable, LocalDateTime start, LocalDateTime end, MovementType type) {
        return stockMovementRepository.findAllProjected( pageable, start, end,type);
    }
}
