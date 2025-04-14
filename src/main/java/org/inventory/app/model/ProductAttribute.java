package org.inventory.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity(name = "product_attributes")
@IdClass(ProductAttributeId.class)
@Data
public class ProductAttribute {

    @Id
    @ManyToOne
    private Product product;

    @Id
    @ManyToOne
    private Attribute attribute;

    private String value;
}
