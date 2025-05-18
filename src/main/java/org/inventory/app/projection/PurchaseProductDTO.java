package org.inventory.app.projection;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PurchaseProductDTO implements Serializable {

    private Long productId;
    private String productName;
    private String sku;
    private BigDecimal unitPrice;

    public PurchaseProductDTO(Long productId, String productName, BigDecimal unitPrice, String sku) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.sku = sku;
    }
}
