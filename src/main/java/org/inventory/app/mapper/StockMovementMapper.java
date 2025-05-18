package org.inventory.app.mapper;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.StockMovementDTO;
import org.inventory.app.model.StockMovement;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockMovementMapper {

    private final ProductMapper productMapper;
    private final WarehouseMapper warehouseMapper;

    public StockMovementDTO toDto(StockMovement entity) {
        if (entity == null) return null;

        return StockMovementDTO.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .productDTO(productMapper.toDto(entity.getProduct()))
                .warehouseDTO(warehouseMapper.toDto(entity.getWarehouse()))
                .quantity(entity.getQuantity())
                .movementType(entity.getMovementType())
                .reason(entity.getReason())
                .createdAt(entity.getCreatedAt())
                .username(entity.getUsername())
                .build();
    }

    public StockMovement toEntity(StockMovementDTO dto) {
        if (dto == null) return null;
        return StockMovement.builder()
                .product(productMapper.toEntity(dto.getProductDTO()))
                .warehouse(warehouseMapper.toEntity(dto.getWarehouseDTO()))
                .quantity(dto.getQuantity())
                .movementType(dto.getMovementType())
                .reason(dto.getReason())
                .username(dto.getUsername())
                .build();
    }
}
