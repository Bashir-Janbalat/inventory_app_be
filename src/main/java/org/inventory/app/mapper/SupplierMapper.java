package org.inventory.app.mapper;

import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {



    public Supplier toEntity(SupplierDTO supplierDTO) {
        if (supplierDTO == null) {
            return null;
        }
        Supplier supplier = new Supplier();
        supplier.setName(supplierDTO.getName());
        supplier.setContactEmail(supplierDTO.getContactEmail());

        return supplier;
    }


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
