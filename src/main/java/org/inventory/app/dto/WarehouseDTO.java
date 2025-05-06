package org.inventory.app.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WarehouseDTO {

    private Long id;

    private String name;
    private String address;

    public WarehouseDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

}
