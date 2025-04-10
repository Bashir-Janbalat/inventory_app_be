package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    private String name;
    private String sku;
    private String description;
    private BigDecimal price;

    private Long categoryID;
    private String categoryName;

    private Long brandID;
    private String brandName;

    private Long supplierID;
    private String supplierName;





}
