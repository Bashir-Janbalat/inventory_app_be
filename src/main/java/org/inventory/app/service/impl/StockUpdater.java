package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.dto.StockDTO;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;
import org.inventory.app.model.Product;
import org.inventory.app.model.Stock;
import org.inventory.app.model.StockMovement;
import org.inventory.app.model.Warehouse;
import org.inventory.app.repository.StockMovementRepository;
import org.inventory.app.repository.WarehouseRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class StockUpdater {

    private final WarehouseRepository warehouseRepository;
    private final StockMovementRepository stockMovementRepository;


    public void updateStocksFromDTO(Product product, ProductDTO dto) {
        if (dto.getStocks() == null || dto.getStocks().isEmpty()) {
            return;
        }
        for (StockDTO stockDTO : dto.getStocks()) {
            if (stockDTO.getMovementQuantity() == null || stockDTO.getMovementType() == null) {
                continue;
            }
            validateStockMovement(stockDTO);
            MovementType movementType = MovementType.valueOf(stockDTO.getMovementType());
            int inputQuantity = stockDTO.getMovementQuantity();
            Stock sourceStock = findExistingStock(product, stockDTO.getWarehouse().getId());
            int oldQuantity = (sourceStock != null) ? sourceStock.getQuantity() : 0;

            if (!isStockMovementValid(oldQuantity, inputQuantity, movementType)) {
                throw new IllegalArgumentException("Invalid quantity for movement type: " + movementType);
            }

            if (movementType == MovementType.TRANSFER) {
                processTransferMovement(product, stockDTO, sourceStock);
            } else {
                processStockMovement(product, stockDTO, sourceStock);
            }
        }
    }

    private void validateStockMovement(StockDTO stockDTO) {
        if (stockDTO.getMovementQuantity() <= 0) {
            throw new IllegalArgumentException("Movement quantity must be greater than zero.");
        }
    }

    private void processTransferMovement(Product product, StockDTO stockDTO, Stock sourceStock) {
        if (sourceStock == null) {
            throw new IllegalArgumentException("Source stock does not exist for transfer.");
        }
        sourceStock.setQuantity(calculateNewQuantity(sourceStock, stockDTO, true));
        Long destinationWarehouseId = stockDTO.getDestinationWarehouseId();
        Warehouse destWarehouse = warehouseRepository.findById(destinationWarehouseId).orElseThrow(() -> new IllegalArgumentException("Destination warehouse not found"));

        Stock destinationStock = findExistingStock(product, destinationWarehouseId);
        Integer movementQuantity = stockDTO.getMovementQuantity();
        if (destinationStock != null) {
            destinationStock.setQuantity(calculateNewQuantity(destinationStock, stockDTO, false));
            createStockMovementFor(destinationStock, movementQuantity, MovementType.IN, MovementReason.RECEIVED_TRANSFER);
            createStockMovementFor(sourceStock, movementQuantity, MovementType.OUT, MovementReason.TRANSFERRED);
        } else {
            Stock newStock = createNewStockForDestination(product, movementQuantity, destWarehouse);
            createStockMovementFor(newStock, movementQuantity, MovementType.IN, MovementReason.CREATED);
            createStockMovementFor(sourceStock, movementQuantity, MovementType.OUT, MovementReason.TRANSFERRED);
        }
    }

    private void createStockMovementFor(Stock stock, Integer movementQuantity, MovementType type, MovementReason reason) {
        String username = getCurrentUserName();
        StockMovement movementDestinationStock = buildStockMovementFromStock(
                stock,
                movementQuantity,
                type,
                reason, username);
        stockMovementRepository.save(movementDestinationStock);
        log.info("'{}' movement saved: '{} {}' units ,warehouse '{}', product '{}', username '{}'", type,reason, movementQuantity,
                stock.getWarehouse().getName(), stock.getProduct().getName(), username);
    }

    private void processStockMovement(Product product, StockDTO stockDTO, Stock sourceStock) {
        if (sourceStock == null) {
            Warehouse warehouse = warehouseRepository.findById(stockDTO.getWarehouse().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));
            sourceStock = new Stock();
            sourceStock.setProduct(product);
            sourceStock.setWarehouse(warehouse);
            sourceStock.setQuantity(0); // Initialwert
            product.getStocks().add(sourceStock);
        }
        sourceStock.setQuantity(calculateNewQuantity(sourceStock, stockDTO, true));
        MovementType movementType = MovementType.valueOf(stockDTO.getMovementType());
        createStockMovementFor(sourceStock, stockDTO.getMovementQuantity(), movementType, mapDefaultReason(movementType));
    }

    private Stock createNewStockForDestination(Product product, Integer movementQuantity, Warehouse destWarehouse) {
        Stock newStock = new Stock();
        newStock.setWarehouse(destWarehouse);
        newStock.setQuantity(movementQuantity);
        newStock.setProduct(product);
        product.getStocks().add(newStock);
        return newStock;
    }

    private Integer calculateNewQuantity(Stock stock, StockDTO stockDTO, boolean isSource) {
        MovementType type = MovementType.valueOf(stockDTO.getMovementType());
        return switch (type) {
            case IN, RETURN -> stock.getQuantity() + stockDTO.getMovementQuantity();
            case OUT, DAMAGED, TRANSFER ->
                    isSource ? stock.getQuantity() - stockDTO.getMovementQuantity() : stock.getQuantity() + stockDTO.getMovementQuantity();
        };
    }

    private boolean isStockMovementValid(int oldQuantity, int inputQuantity, MovementType type) {
        return switch (type) {
            case IN, RETURN -> (oldQuantity + inputQuantity) > oldQuantity;
            case OUT, TRANSFER, DAMAGED -> (oldQuantity - inputQuantity) >= 0;
        };
    }

    private Stock findExistingStock(Product product, Long warehouseId) {
        return product.getStocks().stream().filter(stock -> stock.getWarehouse().getId().equals(warehouseId)).findFirst().orElse(null);
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

    private MovementReason mapDefaultReason(MovementType type) {
        return switch (type) {
            case RETURN -> MovementReason.RETURNED;
            case DAMAGED -> MovementReason.DAMAGED;
            default -> MovementReason.UPDATED;
        };
    }

}

