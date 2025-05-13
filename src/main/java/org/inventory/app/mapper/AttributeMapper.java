package org.inventory.app.mapper;

import org.inventory.app.dto.AttributeDTO;
import org.inventory.app.model.Attribute;
import org.springframework.stereotype.Component;

@Component
public class AttributeMapper {

    public Attribute toEntity(AttributeDTO dto) {
        if (dto == null) {
            return null;
        }
        Attribute attribute = new Attribute();
        attribute.setId(dto.getId());
        attribute.setName(dto.getName());

        return attribute;
    }

    public AttributeDTO toDto(Attribute attribute) {
        if (attribute == null) {
            return null;
        }
        AttributeDTO attributeDTO = new AttributeDTO();
        attributeDTO.setId(attribute.getId());
        attributeDTO.setName(attribute.getName());

        return attributeDTO;
    }

}
