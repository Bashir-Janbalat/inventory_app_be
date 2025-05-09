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
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setPhone(supplierDTO.getPhone());
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
        supplierDTO.setAddress(supplier.getAddress());
        supplierDTO.setPhone(supplier.getPhone());
        return supplierDTO;
    }
}
