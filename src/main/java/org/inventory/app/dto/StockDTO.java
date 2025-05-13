package org.inventory.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

    @Valid
    @Min(value = 0, message = "Quantity must be a non-negative number")
    private int quantity;
    @Valid
    @NotNull(message = "Warehouse is required")
    private WarehouseDTO warehouse;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String movementType;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Integer movementQuantity;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long destinationWarehouseId; // used for transfer

    public StockDTO(int quantity, WarehouseDTO warehouse) {
        this.quantity = quantity;
        this.warehouse = warehouse;
    }
}
