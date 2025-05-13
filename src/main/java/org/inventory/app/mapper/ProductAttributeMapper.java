package org.inventory.app.mapper;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.AttributeDTO;
import org.inventory.app.dto.ProductAttributeDTO;
import org.inventory.app.model.ProductAttribute;
import org.inventory.app.service.AttributeService;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductAttributeMapper {

    private final AttributeService attributeService;
    private final AttributeMapper attributeMapper;

    public ProductAttributeDTO toDto(ProductAttribute productAttribute) {
        ProductAttributeDTO dto = new ProductAttributeDTO();
        dto.setAttributeID(productAttribute.getAttribute().getId());
        dto.setAttributeName(productAttribute.getAttribute().getName());
        dto.setAttributeValue(productAttribute.getValue());
        return dto;
    }

    public ProductAttribute toEntity(ProductAttributeDTO productAttributeDTO) {
        ProductAttribute productAttributeEntity = new ProductAttribute();
        AttributeDTO attribute;
        if (productAttributeDTO.getAttributeID() != null) {
            attribute = attributeService.getAttributeById(productAttributeDTO.getAttributeID());
        } else {
            attribute = attributeService.saveOrGetAttribute(productAttributeDTO.getAttributeName().trim());
        }
        productAttributeEntity.setAttribute(attributeMapper.toEntity(attribute));
        productAttributeEntity.setValue(productAttributeDTO.getAttributeValue());
        return productAttributeEntity;
    }
}
