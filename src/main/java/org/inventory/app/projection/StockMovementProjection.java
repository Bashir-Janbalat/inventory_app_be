package org.inventory.app.projection;

import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;

import java.time.LocalDateTime;

public interface StockMovementProjection {
    Long getId();

    Long getProductId();

    String getProductName();

    Long getWarehouseId();

    String getWarehouseName();

    Integer getQuantity();

    MovementType getMovementType();

    MovementReason getReason();

    LocalDateTime getCreatedAt();

    String getUsername();

    Boolean getProductDeleted();

    String getProductNameSnapshot();
}
