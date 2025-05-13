package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.WarehouseMapper;
import org.inventory.app.model.Warehouse;
import org.inventory.app.repository.WarehouseRepository;
import org.inventory.app.service.WarehouseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehouses", key = "'warehouses'")
    public List<WarehouseDTO> getAllWarehouses() {
        List<WarehouseDTO> warehouseDTOS = warehouseRepository.findAll().stream().map(warehouseMapper::toDto).toList();
        log.info("Fetched {} Warehouses from DB (and cached in warehouses)", warehouseDTOS.size());
        return warehouseDTOS;
    }

    @Override
    @Transactional(readOnly = true)
    @CacheEvict(value = {"warehouses"}, allEntries = true)
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.save(warehouseMapper.toEntity(warehouseDTO));
        log.info("Created new warehouses with ID: {}. Cache 'warehouses' evicted.", warehouse.getId());
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"warehouses"}, allEntries = true)
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("warehouse with ID {} not found for update.", id);
                    return new ResourceNotFoundException("Warehouse with ID '" + id + "' not found.");
                });


        warehouse.setName(warehouseDTO.getName().trim());
        warehouse.setAddress(warehouseDTO.getAddress().trim());
        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Updated warehouse with ID: {}. Cache 'warehouses' evicted.", id);
        return warehouseMapper.toDto(saved);
    }

}
