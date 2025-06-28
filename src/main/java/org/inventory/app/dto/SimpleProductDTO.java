package org.inventory.app.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SimpleProductDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal sellingPrice;
}
