package org.inventory.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.projection.WarehouseStatsDTO;
import org.inventory.app.service.WarehouseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<?> getAllWarehouses(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            PagedResponseDTO<WarehouseDTO> warehouses = warehouseService.getPagedWarehouses(pageable);
            return ResponseEntity.ok(warehouses);
        } else {
            return ResponseEntity.ok(warehouseService.getAllWarehouses());
        }
    }

    @PostMapping
    public ResponseEntity<WarehouseDTO> createWarehouse(@RequestBody @Valid WarehouseDTO warehouseDTO) {
        WarehouseDTO createdWarehouseDTO = warehouseService.createWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDTO> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehous(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDTO> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehousById(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<PagedResponseDTO<WarehouseStatsDTO>>
    getWarehousesWithStats(@RequestParam(defaultValue = "0") Integer page,
                                  @RequestParam(defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        PagedResponseDTO<WarehouseStatsDTO> warehouses = warehouseService.getWarehousesWithStats(pageable);
        return ResponseEntity.ok(warehouses);

    }

}