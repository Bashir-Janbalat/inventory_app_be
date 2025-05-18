package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryStatsDTO implements Serializable {


    private long id;
    private String name;
    private long totalBrands;
    private long totalProducts;
    private long totalStockQuantity;
}
