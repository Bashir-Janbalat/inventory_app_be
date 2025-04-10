package org.inventory.app.mapper;

import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {


    // Konvertiert BrandDTO zu Brand (Entity)
    public Supplier toEntity(SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setId(supplierDTO.getId());
        supplier.setName(supplierDTO.getName());
        supplier.setContactEmail(supplierDTO.getContactEmail());

        return supplier;
    }

    // Konvertiert Brand (Entity) zu BrandDTO
    public SupplierDTO toDto(Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        SupplierDTO supplierDTO = new SupplierDTO();
        supplierDTO.setId(supplier.getId());
        supplierDTO.setName(supplier.getName());
        supplierDTO.setContactEmail(supplier.getContactEmail());

        return supplierDTO;
    }
}
