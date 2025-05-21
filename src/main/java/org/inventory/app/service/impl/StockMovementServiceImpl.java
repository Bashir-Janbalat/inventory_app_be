package org.inventory.app.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;
import org.inventory.app.model.Stock;
import org.inventory.app.model.StockMovement;
import org.inventory.app.projection.StockMovementSummaryDTO;
import org.inventory.app.repository.StockMovementRepository;
import org.inventory.app.service.StockMovementService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

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
        String username = getCurrentUserName();
        StockMovement movementDestinationStock = buildStockMovementFromStock(
                stock,
                movementQuantity,
                type,
                reason, username);
        stockMovementRepository.save(movementDestinationStock);
        log.info("'{}' movement saved: '{} {}' units ,warehouse '{}', product '{}', username '{}'", type, reason, movementQuantity,
                stock.getWarehouse().getName(), stock.getProduct().getName(), username);
    }

    @Override
    public boolean isQuantityForTypeValid(int oldQuantity, int inputQuantity, MovementType type) {
        return switch (type) {
            case IN, RETURN -> (oldQuantity + inputQuantity) > oldQuantity;
            case OUT, TRANSFER, DAMAGED -> (oldQuantity - inputQuantity) >= 0;
        };
    }

    private StockMovement buildStockMovementFromStock(Stock stock, Integer quantity, MovementType movementType,
                                                      MovementReason reason, String username) {
        return StockMovement.builder()
                .product(stock.getProduct())
                .warehouse(stock.getWarehouse())
                .quantity(quantity)
                .movementType(movementType)
                .reason(reason)
                .username(username)
                .build();
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
