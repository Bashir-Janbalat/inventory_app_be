package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.exception.DuplicateResourceException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.SupplierMapper;
import org.inventory.app.model.Supplier;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.SupplierService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"suppliers", "supplier"}, allEntries = true)
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        String name = supplierDTO.getName().trim();
        String email = supplierDTO.getContactEmail().trim();

        supplierRepository.findByNameAndContactEmail(name, email).ifPresent(existingSupplier -> {
            log.warn("Attempt to create duplicate supplier with name '{}' and email '{}'", name, email);
            throw new AlreadyExistsException("Supplier", "name and contactEmail", name + " / " + email);
        });

        Supplier savedSupplier = supplierRepository.save(supplierMapper.toEntity(supplierDTO));
        log.info("Created new supplier with ID: {}. Cache 'suppliers' and 'supplier' evicted.", savedSupplier.getId());
        return supplierMapper.toDto(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "supplier", key = "#id")
    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Supplier with ID {} not found.", id);
                    return new ResourceNotFoundException("Supplier with ID '" + id + "' not found.");
                });

        log.info("Fetched supplier with ID: {} from DB (and cached in 'supplier')", id);
        return supplierMapper.toDto(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "suppliers", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        log.info("Fetched {} suppliers from DB (and cached the page in 'suppliers')", suppliers.getTotalElements());
        return suppliers.map(supplierMapper::toDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"suppliers", "supplier"}, allEntries = true)
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        supplierRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Supplier with ID {} not found for update.", id);
                    return new ResourceNotFoundException("Supplier with ID '" + id + "' not found.");
                });

        String name = supplierDTO.getName().trim();
        String email = supplierDTO.getContactEmail().trim();

        supplierRepository.findByNameAndContactEmail(name, email).ifPresent(duplicateSupplier -> {
            if (!duplicateSupplier.getId().equals(id)) {
                log.warn("Duplicate supplier with name '{}' and email '{}' found for different ID {}", name, email, duplicateSupplier.getId());
                throw new DuplicateResourceException("Supplier with name '" + name + "' and email '" + email + "' already exists.");
            }
        });

        Supplier updated = supplierMapper.toEntity(supplierDTO);
        updated.setId(id);

        Supplier saved = supplierRepository.save(updated);
        log.info("Updated supplier with ID: {}. Cache 'suppliers' and 'supplier' evicted.", id);
        return supplierMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"suppliers", "supplier"}, allEntries = true)
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent supplier with ID {}", id);
            throw new ResourceNotFoundException("Supplier with ID '" + id + "' not found.");
        }

        supplierRepository.deleteById(id);
        log.info("Deleted supplier with ID: {}. Cache 'suppliers' and 'supplier' evicted.", id);
    }

    @Override
    @Cacheable(value = "SupplierCountCache")
    public Long getTotalSupplierCount() {
        long count = supplierRepository.count();
        log.info("Total Supplier count: {}", count);
        return count;
    }
}
