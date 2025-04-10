package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.mapper.SupplierMapper;
import org.inventory.app.model.Supplier;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.SupplierService;
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
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream().map(supplierMapper::toDto).collect(Collectors.toList());
    }
}
