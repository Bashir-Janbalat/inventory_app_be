package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandDTO {

    private Long id;
    private String name;

    public BrandDTO(String name) {
        this.name = name;
    }
}
