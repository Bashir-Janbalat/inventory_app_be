package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity(name = "warehouses")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Warehouse extends BaseEntity {


    private String name;
    private String address;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;

}
