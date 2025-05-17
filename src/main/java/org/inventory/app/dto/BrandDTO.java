package org.inventory.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Builder
public class BrandDTO extends BaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Brand name is required")
    private String name;

    public BrandDTO(String name) {
        this.name = name;
    }
}
