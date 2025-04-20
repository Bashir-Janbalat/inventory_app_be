package org.inventory.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;
    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU cannot be longer than 100 characters")
    private String sku;
    @Size(max = 5000, message = "Description too long")
    private String description;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @NotNull(message = "Category-ID is required")
    private Long categoryID;
    private String categoryName;
    @NotNull(message = "Brand-ID is required")
    private Long brandID;
    private String brandName;
    @NotNull(message = "Supplier-ID is required")
    private Long supplierID;
    private String supplierName;
    private String supplierContactEmail;

    private List<ImageDTO> images = new ArrayList<>();
    private StockDTO stock;
    private List<ProductAttributeDTO> productAttributes = new ArrayList<>();


}
