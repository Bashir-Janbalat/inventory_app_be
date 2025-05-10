package org.inventory.app.mapper;

import org.inventory.app.dto.StockMovementDTO;
import org.inventory.app.model.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    private ProductMapper productMapper;

    public StockMovementDTO toDto(StockMovement entity) {
        if (entity == null) return null;

        return StockMovementDTO.builder()
                .id(entity.getId())
                .productDTO(productMapper.toDto(entity.getProduct()))
                .warehouseId(entity.getWarehouseId())
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
                .warehouseId(dto.getWarehouseId())
                .quantity(dto.getQuantity())
                .movementType(dto.getMovementType())
                .reason(dto.getReason())
                .username(dto.getUsername())
                .build();
    }
}
