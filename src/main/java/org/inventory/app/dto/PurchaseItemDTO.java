package org.inventory.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseItemDTO {
    private Long id;
    @NotNull(message = "ProductID must not be null")
    private Long productId;
    private String productName;
    private String Sku;
    private int quantity;
    private double unitPrice;
    @NotNull(message = "WarehouseID must not be null")
    private Long warehouseId;
    private String warehouseName;
}
