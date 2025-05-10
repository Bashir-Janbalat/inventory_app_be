package org.inventory.app.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class WarehouseDTO implements Serializable {

    @NotNull(message = "Warehouse ID is required")
    private Long id;

    private String name;
    private String address;

    public WarehouseDTO(String name, String address) {
        this.name = name;
        this.address = address;
    }

}
