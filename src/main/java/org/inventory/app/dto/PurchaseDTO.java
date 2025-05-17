package org.inventory.app.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.inventory.app.enums.PurchaseStatus;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseDTO extends BaseDTO {
    @NotNull(message = "SupplierID must not be null")
    private Long supplierId;
    private String supplierName;
    private PurchaseStatus status;
    @NotEmpty(message = "Purchase items must not be empty")
    private List<PurchaseItemDTO> items;
}
