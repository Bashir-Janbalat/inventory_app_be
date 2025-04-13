package org.inventory.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Product product;

    private int quantity;

    private String warehouseLocation;
}
