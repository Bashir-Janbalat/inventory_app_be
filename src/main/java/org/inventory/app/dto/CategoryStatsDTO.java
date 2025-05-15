package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CategoryStatsDTO implements Serializable {


    private Long id;
    private String name;
    private long totalBrands;
    private long totalProducts;
    private long totalStockQuantity;
}
