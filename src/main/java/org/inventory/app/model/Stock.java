package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "stock")
@Data
@NoArgsConstructor
public class Stock {
    @Id
    private Long productId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private String warehouseLocation;

    public Stock(Product product, int quantity, String warehouseLocation) {
        this.product = product;
        this.quantity = quantity;
        this.warehouseLocation = warehouseLocation;
    }
}
