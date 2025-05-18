package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {

    ValueWrapper<List<WarehouseDTO>> getAllWarehouses();

    PagedResponseDTO<WarehouseDTO> getPagedWarehouses(Pageable pageable);

    WarehouseDTO createWarehouse(WarehouseDTO warehouse);

    WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO);

    void deleteWarehous(Long id);

    WarehouseDTO getWarehousById(Long id);

    PagedResponseDTO<WarehouseStatsDTO> getWarehousesWithStats(Pageable pageable);
}

