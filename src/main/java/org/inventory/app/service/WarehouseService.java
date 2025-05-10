package org.inventory.app.service;

import org.inventory.app.dto.WarehouseDTO;

import java.util.List;

public interface WarehouseService {

    List<WarehouseDTO> getAllWarehouses();
    WarehouseDTO createWarehouse(WarehouseDTO warehouse);
}

