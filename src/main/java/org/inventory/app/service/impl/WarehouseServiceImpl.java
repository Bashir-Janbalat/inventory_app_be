package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.exception.EntityHasAssociatedItemsException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.WarehouseMapper;
import org.inventory.app.model.Warehouse;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.inventory.app.repository.StockRepository;
import org.inventory.app.repository.WarehouseRepository;
import org.inventory.app.service.WarehouseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final StockRepository stockRepository;


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehouses")
    public ValueWrapper<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouseDTOS = warehouseRepository.findAll().stream().map(warehouseMapper::toDto).toList();
        log.info("Fetched {} Warehouses from DB (and cached in warehouses)", warehouseDTOS.size());
        return new ValueWrapper<>(warehouseDTOS);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "pagedWarehouses", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<WarehouseDTO> getPagedWarehouses(Pageable pageable) {
        Page<Warehouse> warehouses = warehouseRepository.findAll(pageable);
        log.info("Fetched {} warehouses from DB (page {} size {}) (and cached in pagedWarehouses)",
                warehouses.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(warehouses.map(warehouseMapper::toDto));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"warehouses", "pagedWarehouses", "warehousesStats"}, allEntries = true)
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouse = warehouseRepository.save(warehouseMapper.toEntity(warehouseDTO));
        log.info("Created new warehouses with ID: {}. Cache 'warehouses' evicted.", warehouse.getId());
        return warehouseMapper.toDto(warehouse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"warehouses", "pagedWarehouses", "warehousesStats"}, allEntries = true)
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

    @Override
    @Transactional
    @CacheEvict(value = {"warehouses", "pagedWarehouses", "warehousesStats"}, allEntries = true)
    public void deleteWarehous(Long id) {
        if (!warehouseRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent Warehous with ID {}", id);
            throw new ResourceNotFoundException("Warehous with ID '" + id + "' not found.");
        }
        if (stockRepository.existsByWarehouseId(id)) {
            log.warn("Attempted to delete Warehous with ID {} that has associated stocks.", id);
            throw new EntityHasAssociatedItemsException("Warehous", id);
        }

        warehouseRepository.deleteById(id);
        log.info("Deleted Warehous with ID: {}. Cache 'warehouses', 'pagedWarehouses' evicted.", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehous", key = "#id")
    public WarehouseDTO getWarehousById(Long id) {
        WarehouseDTO warehouseDTO = warehouseRepository.findById(id).map(warehouseMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Warehous with ID {} not found.", id);
                    return new ResourceNotFoundException("Warehous with ID '" + id + "' not found.");
                });
        log.info("Fetched warehous with ID: {} from DB (and cached in 'warehous')", id);
        return warehouseDTO;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehousesStats", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<WarehouseStatsDTO> getWarehousesWithStats(Pageable pageable) {
        Page<WarehouseStatsDTO> warehouses = warehouseRepository.fetchWarehouseStatsWithTotalQuantity(pageable);
        log.info("Fetched {} warehouses with stats from DB (page {} size {}) (and cached in warehousesStats)",
                warehouses.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(warehouses);
    }
}

