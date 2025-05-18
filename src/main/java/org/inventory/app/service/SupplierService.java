package org.inventory.app.service;


import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.SupplierDTO;
import org.springframework.data.domain.Pageable;

public interface SupplierService {

    SupplierDTO createSupplier(SupplierDTO SupplierDTO);
    SupplierDTO getSupplierById(Long id);
    PagedResponseDTO<SupplierDTO> getAllSuppliers(Pageable pageable);
    SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO);
    void deleteSupplier(Long id);
    ValueWrapper<Long> getTotalSupplierCount();
}
