package org.inventory.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductAttributeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long attributeID;
    private String attributeName;
    private String attributeValue;

    public ProductAttributeDTO(String attributeName, String attributeValue) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
    }
}
