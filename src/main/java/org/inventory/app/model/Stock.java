package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "stock")
@Data
public class Stock {
    @Id
    private Long productId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private String warehouseLocation;
}
