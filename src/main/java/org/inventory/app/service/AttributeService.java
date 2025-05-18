package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.AttributeDTO;

import java.util.List;

public interface AttributeService {

    AttributeDTO saveOrGetAttribute(String name);

    AttributeDTO getAttributeById(Long attributeID);

    ValueWrapper<List<AttributeDTO>> getAttributes();
}
