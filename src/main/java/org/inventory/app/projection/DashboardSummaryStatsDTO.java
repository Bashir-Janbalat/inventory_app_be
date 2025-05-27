package org.inventory.app.projection;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardSummaryStatsDTO implements Serializable {
    private long totalProducts;
    private long totalActiveProducts;
    private long totalCategories;
    private long totalBrands;
    private long totalSuppliers;
    private long totalWarehouses;
    private long totalStockQuantity;
}
