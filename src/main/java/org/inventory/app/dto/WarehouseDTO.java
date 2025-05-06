package org.inventory.app.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class WarehouseDTO implements Serializable {

    private Long id;

    private String name;
    private String address;

    public WarehouseDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

}
