package org.inventory.app.mapper;

import org.inventory.app.dto.ProductAttributeDTO;
import org.inventory.app.model.Attribute;
import org.inventory.app.model.ProductAttribute;
import org.springframework.stereotype.Component;

@Component
public class ProductAttributeMapper {

    public ProductAttributeDTO toDto(ProductAttribute productAttribute) {
        ProductAttributeDTO dto = new ProductAttributeDTO();
        dto.setAttributeID(productAttribute.getAttribute().getId());
        dto.setAttributeName(productAttribute.getAttribute().getName());
        dto.setAttributeValue(productAttribute.getValue());
        return dto;
    }

    public ProductAttribute toEntity(ProductAttributeDTO productAttributeDTO) {
        ProductAttribute productAttributeEntity = new ProductAttribute();
        Attribute attribute = new Attribute();
        attribute.setName(productAttributeDTO.getAttributeName());
        productAttributeEntity.setAttribute(attribute);
        productAttributeEntity.setValue(productAttributeDTO.getAttributeValue());
        return productAttributeEntity;
    }
}
