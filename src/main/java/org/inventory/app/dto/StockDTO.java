package org.inventory.app.dto;

import lombok.Data;

@Data
public class StockDTO {

    private int quantity;

    private String warehouseLocation;
}
