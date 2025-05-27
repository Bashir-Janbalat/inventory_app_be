package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.projection.BrandStatsDTO;
import org.inventory.app.projection.CategoryStatsDTO;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.repository.WarehouseRepository;
import org.inventory.app.service.DashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Cacheable(value = "brandStats", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public PagedResponseDTO<BrandStatsDTO> findBrandsWithStats(Pageable pageable) {
        Page<BrandStatsDTO> brands = brandRepository.findBrandsWithStats(pageable);
        log.info("Fetched {} brands with stats from DB (page {} size {}) (and cached in brandStats)",
                brands.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(brands);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryStats", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<CategoryStatsDTO> findCategoriesWithStats(Pageable pageable) {
        Page<CategoryStatsDTO> categories = categoryRepository.findCategoryStats(pageable);
        log.info("Fetched {} categories with stats (page {} size {}) from DB (and cached in 'categoryStats')", categories.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(categories);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "warehouseStats", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<WarehouseStatsDTO> findWarehousesWithStats(Pageable pageable) {
        Page<WarehouseStatsDTO> warehouses = warehouseRepository.fetchWarehouseStatsWithTotalQuantity(pageable);
        log.info("Fetched {} warehouses with stats from DB (page {} size {}) (and cached in warehousesStats)",
                warehouses.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(warehouses);
    }
}
