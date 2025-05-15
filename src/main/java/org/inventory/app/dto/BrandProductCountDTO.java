package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrandProductCountDTO implements Serializable {

    private Long id;
    private String name;
    private Long productCount;
    private Long totalStock;

}
