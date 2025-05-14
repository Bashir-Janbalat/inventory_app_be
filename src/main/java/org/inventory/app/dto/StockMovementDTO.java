package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovementDTO implements Serializable {

    private Long id;
    private ProductDTO productDTO;
    private WarehouseDTO warehouseDTO;
    private Integer quantity;
    private MovementType movementType;
    private MovementReason reason;
    private LocalDateTime createdAt;
    private String username;
}
