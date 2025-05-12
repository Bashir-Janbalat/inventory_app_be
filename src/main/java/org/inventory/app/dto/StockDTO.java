package org.inventory.app.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int quantity;
    @NotNull(message = "Warehouse is required")
    @Valid
    private WarehouseDTO warehouse;
    private String movementType;
    private Integer movementQuantity;
    private Long destinationWarehouseId; // used for transfer

    public StockDTO(int quantity, WarehouseDTO warehouse) {
        this.quantity = quantity;
        this.warehouse = warehouse;
    }
}
