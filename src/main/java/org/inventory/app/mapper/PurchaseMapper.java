package org.inventory.app.mapper;


import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.Purchase;
import org.inventory.app.model.Supplier;
import org.inventory.app.repository.SupplierRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class PurchaseMapper {

    private final SupplierRepository supplierRepository;
    private final PurchaseItemMapper itemMapper;

    public PurchaseDTO toDto(Purchase entity) {
        if (entity == null) return null;

        PurchaseDTO dto = new PurchaseDTO();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setSupplierId(entity.getSupplier().getId());
        dto.setSupplierName(entity.getSupplier().getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setStatus(entity.getStatus());
        dto.setItems(entity.getItems().stream().map(itemMapper::toDto).collect(Collectors.toList()));
        return dto;
    }

    public Purchase toEntity(PurchaseDTO dto) {
        if (dto == null) return null;

        Purchase entity = new Purchase();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());

        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + dto.getSupplierId()));
        entity.setSupplier(supplier);

        if (dto.getItems() != null) {
            entity.setItems(dto.getItems().stream().map(itemMapper::toEntity).collect(Collectors.toList()));
            entity.getItems().forEach(i -> i.setPurchase(entity));
        }

        return entity;
    }
}

