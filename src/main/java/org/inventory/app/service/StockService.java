package org.inventory.app.service;

import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.MovementType;
import org.inventory.app.model.Product;
import org.inventory.app.projection.StockMovementProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface StockService {
    void updateStocksFromDTO(Product product, ProductDTO dto);

    Page<StockMovementProjection> getStockMovements(Pageable pageable, LocalDateTime start, LocalDateTime end, MovementType type);
}
