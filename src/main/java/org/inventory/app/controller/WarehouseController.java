package org.inventory.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.service.WarehouseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
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
}