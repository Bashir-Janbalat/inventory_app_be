package org.inventory.app.dto;

import lombok.*;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StockMovementDTO extends BaseDTO implements Serializable {

    private ProductDTO productDTO;
    private WarehouseDTO warehouseDTO;
    private Integer quantity;
    private MovementType movementType;
    private MovementReason reason;
    private String username;

    @Builder
    public StockMovementDTO(Long id, LocalDateTime createdAt, LocalDateTime updatedAt, ProductDTO productDTO,
                            WarehouseDTO warehouseDTO, Integer quantity, MovementType movementType, MovementReason reason, String username) {
        super(id, createdAt, updatedAt);
        this.productDTO = productDTO;
        this.warehouseDTO = warehouseDTO;
        this.quantity = quantity;
        this.movementType = movementType;
        this.reason = reason;
        this.username = username;
    }
}
