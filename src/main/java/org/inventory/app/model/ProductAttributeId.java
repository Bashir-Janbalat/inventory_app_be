package org.inventory.app.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeId implements Serializable {

    private Long product;
    private Long attribute;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProductAttributeId that = (ProductAttributeId) o;
        return Objects.equals(product, that.product) && Objects.equals(attribute, that.attribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, attribute);
    }
}
