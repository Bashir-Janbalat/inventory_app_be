package org.inventory.app.projection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseStatsDTO implements Serializable {

    private long id;
    private String name;
    private String address;
    private long productCount;
    private long totalStockQuantity;
}
