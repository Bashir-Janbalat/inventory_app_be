package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.projection.*;
import org.inventory.app.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for dashboard statistics and summaries")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get brand statistics with product counts",
            description = "Retrieve paginated list of brands with associated product counts")
    @GetMapping("/brand-stats")
    public ResponseEntity<PagedResponseDTO<BrandStatsDTO>> getAllBrandsWithProductCount(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = createPageable(page, size, sortDirection);
        PagedResponseDTO<BrandStatsDTO> brands = dashboardService.findBrandsWithStats(pageable);
        return ResponseEntity.ok(brands);
    }

    @Operation(summary = "Get category statistics",
            description = "Retrieve paginated list of categories with associated statistics")
    @GetMapping("/category-stats")
    public ResponseEntity<PagedResponseDTO<CategoryStatsDTO>> getCategoriesStats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = createPageable(page, size, sortDirection);
        PagedResponseDTO<CategoryStatsDTO> statsPage = dashboardService.findCategoriesWithStats(pageable);
        return ResponseEntity.ok(statsPage);
    }

    @Operation(summary = "Get warehouse statistics",
            description = "Retrieve paginated list of warehouses with statistics")
    @GetMapping("/warehouse-stats")
    public ResponseEntity<PagedResponseDTO<WarehouseStatsDTO>>
    getWarehousesWithStats(@RequestParam(defaultValue = "0") Integer page,
                           @RequestParam(defaultValue = "10") Integer size,
                           @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = createPageable(page, size, sortDirection);
        PagedResponseDTO<WarehouseStatsDTO> warehouses = dashboardService.findWarehousesWithStats(pageable);
        return ResponseEntity.ok(warehouses);

    }

    @Operation(summary = "Get dashboard summary",
            description = "Retrieve summarized statistics for the dashboard overview")
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryStatsDTO> getSummary() {
        DashboardSummaryStatsDTO summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Get product status summary",
            description = "Retrieve counts of products grouped by their status")
    @GetMapping("/product-status-summary")
    public ResponseEntity<List<ProductStatusCountStatsDTO>> getProductStatusSummary() {
        return ResponseEntity.ok(dashboardService.countProductsByStatus().getValue());
    }

    @Operation(summary = "Get stock status summary",
            description = "Retrieve counts of stock items grouped by their stock status")
    @GetMapping("/stock-status-summary")
    public ResponseEntity<List<StockStatusCountStatsDTO>> getStockStatusSummary() {
        return ResponseEntity.ok(dashboardService.getStockStatusSummary().getValue());
    }

    private Pageable createPageable(int page, int size, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by("name").descending() :
                Sort.by("name").ascending();
        return PageRequest.of(page, size, sort);
    }

    @Operation(summary = "Get monthly product count statistics",
            description = "Retrieve monthly aggregated counts of products")
    @GetMapping("/monthly-product-counts")
    public ResponseEntity<List<MonthlyProductCountStatsDTO>> getMonthlyProductCounts() {
        List<MonthlyProductCountStatsDTO> counts = dashboardService.findMonthlyProductCountStats().getValue();
        return ResponseEntity.ok(counts);
    }
}
