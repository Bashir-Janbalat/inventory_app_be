package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.inventory.app.enums.ProductStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusCountStatsDTO {
    private ProductStatus status;
    private long count;
}
