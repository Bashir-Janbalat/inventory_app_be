package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.enums.ProductStatus;
import org.inventory.app.enums.PurchaseStatus;
import org.inventory.app.projection.PurchaseProductDTO;
import org.inventory.app.service.PurchaseService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/inventory/api/purchases")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Purchases", description = "Endpoints for managing purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    @Operation(summary = "Create a new purchase (ADMIN role required)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody PurchaseDTO purchaseDTO) {
        log.info("Request to create Purchase: {}", purchaseDTO);
        PurchaseDTO created = purchaseService.createPurchase(purchaseDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Get paginated purchases with optional filtering by date")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<PurchaseDTO>>
    getAllPurchases(@RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size,
                    @RequestParam(defaultValue = "createdAt") String sortBy,
                    @RequestParam(defaultValue = "asc") String sortDirection,
                    @RequestParam(required = false) LocalDate date) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        log.info("Request to get all purchases - page: {}, size: {}", page, size);
        PagedResponseDTO<PurchaseDTO> purchases = purchaseService.getAllPurchases(pageable, date);
        return ResponseEntity.ok(purchases);
    }

    @Operation(summary = "Get purchase details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Long id) {
        log.info("Request to get purchase by id: {}", id);
        PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchaseDTO);
    }

    @Operation(summary = "Update status of a purchase (ADMIN role required)")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updatePurchaseStatus(@PathVariable Long id, @RequestParam PurchaseStatus status) {
        log.info("Request to update purchase status - id: {}, status: {}", id, status);
        purchaseService.updatePurchaseStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get products supplied by a specific supplier")
    @GetMapping("/supplierProducts")
    public ResponseEntity<List<PurchaseProductDTO>> getProductsForSupplier(@RequestParam Long supplierId) {
        ValueWrapper<List<PurchaseProductDTO>> productsForSupplier = purchaseService.getProductsForSupplier(supplierId);
        return ResponseEntity.ok(productsForSupplier.getValue());
    }

    @Operation(summary = "Get products filtered by product status")
    @GetMapping("/statusProducts")
    public ResponseEntity<List<PurchaseProductDTO>> getProductsByStatus(@RequestParam ProductStatus productStatus) {
        ValueWrapper<List<PurchaseProductDTO>> productsForSupplier = purchaseService.getProductsByStatus(productStatus);
        return ResponseEntity.ok(productsForSupplier.getValue());
    }
}
