package org.inventory.app.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class WarehouseDTO extends BaseDTO implements Serializable {

    private String name;
    private String address;

    public WarehouseDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

}
