package org.inventory.app.service;

import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    List<WarehouseDTO> getAllWarehouses();

    Page<WarehouseDTO> getPagedWarehouses(Pageable pageable);

    WarehouseDTO createWarehouse(WarehouseDTO warehouse);

    WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO);

    void deleteWarehous(Long id);

    WarehouseDTO getWarehousById(Long id);

    Page<WarehouseStatsDTO> getWarehousesWithStats(Pageable pageable);
}

