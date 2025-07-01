package org.inventory.app.common;

import lombok.Data;
import org.inventory.app.enums.ProductStatus;

@Data
public class ProductSearchFilter {
    private String searchBy = "";
    private String categoryName = "";
    private String brandName = "";
    private String supplierName = "";
    private Integer minPrice;
    private Integer maxPrice;
    private ProductStatus productStatus;
    private Boolean inStock;
    private String sortBy = "name";
    private String sortDirection = "asc";
}