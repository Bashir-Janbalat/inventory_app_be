package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;
import org.inventory.app.model.Product;
import org.inventory.app.model.Stock;
import org.inventory.app.projection.StockMovementSummaryDTO;
import org.inventory.app.repository.StockMovementRepository;
import org.inventory.app.service.StockService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "stockMovements", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<StockMovementSummaryDTO> getStockMovements(Pageable pageable, LocalDateTime start, LocalDateTime end, MovementType type) {
        Page<StockMovementSummaryDTO> stockMovementProjections = stockMovementRepository.findAllProjected(pageable, start, end, type);
        return new PagedResponseDTO<>(stockMovementProjections);
    }

    @Override
    @CacheEvict(value = {"stockMovements"}, allEntries = true)
    public void createStockMovementFor(Stock stock, Integer movementQuantity, MovementType type, MovementReason reason) {
        stockUpdater.createStockMovementFor(stock, movementQuantity, type, reason);
    }

}
