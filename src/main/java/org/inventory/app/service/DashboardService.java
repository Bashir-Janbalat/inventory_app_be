package org.inventory.app.service;

import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.projection.BrandStatsDTO;
import org.inventory.app.projection.CategoryStatsDTO;
import org.inventory.app.projection.DashboardSummaryStatsDTO;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.springframework.data.domain.Pageable;

public interface DashboardService {

    PagedResponseDTO<BrandStatsDTO> findBrandsWithStats(Pageable pageable);
    PagedResponseDTO<CategoryStatsDTO> findCategoriesWithStats(Pageable pageable);
    PagedResponseDTO<WarehouseStatsDTO> findWarehousesWithStats(Pageable pageable);
    DashboardSummaryStatsDTO getDashboardSummary();
}
