package org.inventory.app.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Builder
public class CategoryDTO extends BaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;

    public CategoryDTO(String name) {
        this.name = name;
    }
}
