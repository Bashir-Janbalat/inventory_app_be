package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.enums.StockStatus;
import org.inventory.app.projection.*;
import org.inventory.app.repository.*;
import org.inventory.app.service.DashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final SupplierRepository supplierRepository;

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

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardSummary")
    public DashboardSummaryStatsDTO getDashboardSummary() {
        long totalProducts = productRepository.count();
        long totalCategories = categoryRepository.count();
        long totalBrands = brandRepository.count();
        long totalSuppliers = supplierRepository.count();
        long totalWarehouses = warehouseRepository.count();

        Long totalStockQuantity = stockRepository.sumAllStockQuantities();
        if (totalStockQuantity == null) totalStockQuantity = 0L;

        DashboardSummaryStatsDTO summary = DashboardSummaryStatsDTO.builder()
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .totalBrands(totalBrands)
                .totalSuppliers(totalSuppliers)
                .totalWarehouses(totalWarehouses)
                .totalStockQuantity(totalStockQuantity)
                .build();
        log.info("Dashboard summary: {}", summary);
        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("productStatusSummary")
    public ValueWrapper<List<ProductStatusCountStatsDTO>> countProductsByStatus() {
        List<ProductStatusCountStatsDTO> result = productRepository.countProductsByStatus();
        log.info("Fetched product status summary: {}", result);
        return new ValueWrapper<>(result);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable("stockStatusSummary")
    public ValueWrapper<List<StockStatusCountStatsDTO>> getStockStatusSummary() {
        long outOfStockCount = stockRepository.countProductsOutOfStock();
        long totalProducts = productRepository.count();
        long lowStockCount = stockRepository.countProductsLowStock(10L);
        long inStockCount = totalProducts - outOfStockCount - lowStockCount;


        return new ValueWrapper<>(List.of(
                new StockStatusCountStatsDTO(StockStatus.IN_STOCK, inStockCount),
                new StockStatusCountStatsDTO(StockStatus.OUT_OF_STOCK, outOfStockCount),
                new StockStatusCountStatsDTO(StockStatus.LOW_STOCK, lowStockCount))
        );
    }
}
