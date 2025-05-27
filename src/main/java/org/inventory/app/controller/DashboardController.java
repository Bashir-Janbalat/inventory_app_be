package org.inventory.app.controller;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.projection.BrandStatsDTO;
import org.inventory.app.projection.CategoryStatsDTO;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.inventory.app.service.DashboardService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/brand-stats")
    public ResponseEntity<PagedResponseDTO<BrandStatsDTO>> getAllBrandsWithProductCount(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = createPageable(page, size, sortDirection);
        PagedResponseDTO<BrandStatsDTO> brands = dashboardService.findBrandsWithStats(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/category-stats")
    public ResponseEntity<PagedResponseDTO<CategoryStatsDTO>> getCategoriesStats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        Pageable pageable = createPageable(page, size, sortDirection);
        PagedResponseDTO<CategoryStatsDTO> statsPage = dashboardService.findCategoriesWithStats(pageable);
        return ResponseEntity.ok(statsPage);
    }

    @GetMapping("/warehouse-stats")
    public ResponseEntity<PagedResponseDTO<WarehouseStatsDTO>>
    getWarehousesWithStats(@RequestParam(defaultValue = "0") Integer page,
                           @RequestParam(defaultValue = "10") Integer size,
                           @RequestParam(defaultValue = "asc") String sortDirection) {
        Pageable pageable = createPageable(page, size, sortDirection);
        PagedResponseDTO<WarehouseStatsDTO> warehouses = dashboardService.findWarehousesWithStats(pageable);
        return ResponseEntity.ok(warehouses);

    }

    private Pageable createPageable(int page, int size, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by("name").descending() :
                Sort.by("name").ascending();
        return PageRequest.of(page, size, sort);
    }
}
