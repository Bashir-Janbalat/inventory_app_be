package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.SupplierMapper;
import org.inventory.app.model.Supplier;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.SupplierService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        Supplier savedSupplier = supplierRepository.save(supplierMapper.toEntity(supplierDTO));
        return supplierMapper.toDto(savedSupplier);
    }

    @Override
    public SupplierDTO getSupplierById(Long id) {
        return supplierMapper.toDto(supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier with ID '" + id + "' not found.")));

    }

    @Override
    public List<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable).stream().map(supplierMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier with ID '" + id + "' not found."));

        Supplier updated = supplierMapper.toEntity(supplierDTO);
        updated.setId(id);

        Supplier saved = supplierRepository.save(updated);
        return supplierMapper.toDto(saved);
    }

    @Override
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier with ID '" + id + "' not found.");
        }
        supplierRepository.deleteById(id);
    }
}
