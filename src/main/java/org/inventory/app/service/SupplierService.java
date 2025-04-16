package org.inventory.app.service;


import org.inventory.app.dto.SupplierDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SupplierService {

    SupplierDTO createSupplier(SupplierDTO SupplierDTO);
    SupplierDTO getSupplierById(Long id);
    List<SupplierDTO> getAllSuppliers(Pageable pageable);
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);
    void deleteSupplier(Long id);
}
