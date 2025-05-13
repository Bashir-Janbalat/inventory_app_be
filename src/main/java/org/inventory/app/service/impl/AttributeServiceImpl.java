package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.AttributeDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.AttributeMapper;
import org.inventory.app.model.Attribute;
import org.inventory.app.repository.AttributeRepository;
import org.inventory.app.service.AttributeService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(value = {"attribute","attributes"}, allEntries = true)
    public Attribute saveOrGetAttribute(String name) {
        Optional<Attribute> attribute = attributeRepository.findFirstByName(name);
        if (attribute.isPresent()) {
            return attribute.get();
        }
        Attribute newAttribute = new Attribute(name);
        return attributeRepository.save(newAttribute);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "attribute", key = "#attributeID")
    public Attribute getAttributeById(Long attributeID) {
        return attributeRepository.findById(attributeID)
                .orElseThrow(() -> new ResourceNotFoundException("not Attribute found with id : " + attributeID));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "attributes")
    public List<AttributeDTO> getAttributes() {
        return attributeRepository.findAll().stream().map(attributeMapper::toDto).toList();
    }
}
