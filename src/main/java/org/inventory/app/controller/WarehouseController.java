package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.service.WarehouseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/api/warehouses")
@RequiredArgsConstructor
@Tag(name = "Warehouses", description = "Operations related to warehouse management")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Operation(summary = "Get all warehouses", description = "Returns all warehouses, optionally paginated.")
    @GetMapping
    public ResponseEntity<?> getAllWarehouses(@RequestParam(required = false) Integer page,
                                              @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
            PagedResponseDTO<WarehouseDTO> warehouses = warehouseService.getPagedWarehouses(pageable);
            return ResponseEntity.ok(warehouses);
        } else {
            return ResponseEntity.ok(warehouseService.getAllWarehouses().getValue());
        }
    }

    @Operation(summary = "Create a new warehouse")
    @PostMapping
    public ResponseEntity<WarehouseDTO> createWarehouse(@RequestBody @Valid WarehouseDTO warehouseDTO) {
        WarehouseDTO createdWarehouseDTO = warehouseService.createWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWarehouseDTO);
    }

    @Operation(summary = "Update warehouse information by ID")
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDTO> updateWarehouse(@PathVariable Long id, @RequestBody WarehouseDTO warehouseDTO) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDTO));
    }

    @Operation(summary = "Delete a warehouse by ID (ADMIN role required)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehous(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get warehouse details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDTO> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehousById(id));
    }
}