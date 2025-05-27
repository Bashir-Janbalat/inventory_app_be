package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.inventory.app.enums.StockStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusCountStatsDTO {
    private StockStatus status;
    private Long count;

}