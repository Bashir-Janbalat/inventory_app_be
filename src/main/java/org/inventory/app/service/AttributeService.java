package org.inventory.app.service;

import org.inventory.app.dto.AttributeDTO;
import org.inventory.app.model.Attribute;

import java.util.List;

public interface AttributeService {

    Attribute saveOrGetAttribute(String name);

    Attribute getAttributeById(Long attributeID);

    List<AttributeDTO> getAttributes();
}
