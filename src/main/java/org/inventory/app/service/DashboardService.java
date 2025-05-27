package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.projection.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DashboardService {

    PagedResponseDTO<BrandStatsDTO> findBrandsWithStats(Pageable pageable);

    PagedResponseDTO<CategoryStatsDTO> findCategoriesWithStats(Pageable pageable);

    PagedResponseDTO<WarehouseStatsDTO> findWarehousesWithStats(Pageable pageable);

    DashboardSummaryStatsDTO getDashboardSummary();

    ValueWrapper<List<ProductStatusCountStatsDTO>> countProductsByStatus();

    ValueWrapper<List<StockStatusCountStatsDTO>> getStockStatusSummary();
}
