package org.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Brand name is required")
    private String name;

    public BrandDTO(String name) {
        this.name = name;
    }
}
