package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.inventory.app.model.Image;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    private List<Image> images = new ArrayList<>();





}
