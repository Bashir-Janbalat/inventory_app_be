package org.inventory.app.service;

import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;
import org.inventory.app.model.Stock;
import org.inventory.app.projection.StockMovementSummaryDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface StockMovementService {

    void createStockMovementFor(Stock stock, Integer movementQuantity, MovementType type, MovementReason reason);

    boolean isQuantityForTypeValid(int oldQuantity, int inputQuantity, MovementType type);

    PagedResponseDTO<StockMovementSummaryDTO> getStockMovements(Pageable pageable, LocalDateTime start, LocalDateTime end, MovementType type);
}
