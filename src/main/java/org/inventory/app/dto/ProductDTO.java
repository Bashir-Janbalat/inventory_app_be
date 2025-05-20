package org.inventory.app.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO extends BaseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "SKU is required")
    @Size(max = 100, message = "SKU cannot be longer than 100 characters")
    private String sku;
    @Size(max = 5000, message = "Description too long")
    private String description;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal costPrice;

    private BigDecimal sellingPrice;
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
    @Builder.Default
    private List<ImageDTO> images = new ArrayList<>();
    @Builder.Default
    private List<StockDTO> stocks = new ArrayList<>();
    @Builder.Default
    private List<ProductAttributeDTO> productAttributes = new ArrayList<>();


}
