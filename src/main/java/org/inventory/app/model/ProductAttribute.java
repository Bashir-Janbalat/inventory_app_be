package org.inventory.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "product_attributes")
@IdClass(ProductAttributeId.class)
@Data
@NoArgsConstructor
public class ProductAttribute {

    @Id
    @ManyToOne
    private Product product;

    @Id
    @ManyToOne
    private Attribute attribute;

    @Column(name = "attribute_value")
    private String value;

    public ProductAttribute(Product product, Attribute attribute, String value) {
        this.product = product;
        this.attribute = attribute;
        this.value = value;
    }
}
