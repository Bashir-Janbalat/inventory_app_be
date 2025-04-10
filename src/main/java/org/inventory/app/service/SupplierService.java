package org.inventory.app.service;



import org.inventory.app.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {

    SupplierDTO createSupplier(SupplierDTO SupplierDTO);
    List<SupplierDTO> getAllSuppliers();
}
