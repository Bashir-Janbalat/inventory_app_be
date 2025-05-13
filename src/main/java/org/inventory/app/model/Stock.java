package org.inventory.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "stock")
@IdClass(StockId.class)
@Data
@NoArgsConstructor
public class Stock {

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private int quantity;


}
