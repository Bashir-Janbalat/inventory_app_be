package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementSummaryDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private MovementType movementType;
    private MovementReason reason;
    private LocalDateTime createdAt;
    private String username;
    private Boolean productDeleted;
    private String productNameSnapshot;
}
