package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.SupplierMapper;
import org.inventory.app.model.Supplier;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.SupplierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    @Transactional
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier savedSupplier = supplierRepository.save(supplierMapper.toEntity(supplierDTO));
        return supplierMapper.toDto(savedSupplier);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        return supplierMapper.toDto(supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier with ID '" + id + "' not found.")));

    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findAll(pageable);
        return suppliers.map(supplierMapper::toDto);
    }

    @Override
    @Transactional
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
         supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with ID '" + id + "' not found."));
        String name = supplierDTO.getName().trim();
        String email = supplierDTO.getName().trim();
         supplierRepository.findByNameAndContactEmail(name,email).ifPresent(existingSupplier -> {
             if (!existingSupplier.getId().equals(id)) {
                 throw new ResourceNotFoundException("Supplier name '" + supplierDTO.getName() + "' already exists.");
             }
         });
        Supplier updated = supplierMapper.toEntity(supplierDTO);
        updated.setId(id);

        Supplier saved = supplierRepository.save(updated);
        return supplierMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier with ID '" + id + "' not found.");
        }
        supplierRepository.deleteById(id);
    }
}
