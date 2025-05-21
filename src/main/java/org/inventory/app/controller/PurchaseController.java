package org.inventory.app.controller;

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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
@Slf4j
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseDTO> createPurchase(@Valid @RequestBody PurchaseDTO purchaseDTO) {
        log.info("Request to create Purchase: {}", purchaseDTO);
        PurchaseDTO created = purchaseService.createPurchase(purchaseDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

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

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Long id) {
        log.info("Request to get purchase by id: {}", id);
        PurchaseDTO purchaseDTO = purchaseService.getPurchaseById(id);
        return ResponseEntity.ok(purchaseDTO);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updatePurchaseStatus(@PathVariable Long id, @RequestParam PurchaseStatus status) {
        log.info("Request to update purchase status - id: {}, status: {}", id, status);
        purchaseService.updatePurchaseStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/supplierProducts")
    public ResponseEntity<List<PurchaseProductDTO>> getProductsForSupplier(@RequestParam Long supplierId) {
        ValueWrapper<List<PurchaseProductDTO>> productsForSupplier = purchaseService.getProductsForSupplier(supplierId);
        return ResponseEntity.ok(productsForSupplier.getValue());
    }
    @GetMapping("/statusProducts")
    public ResponseEntity<List<PurchaseProductDTO>> getProductsByStatus(@RequestParam ProductStatus productStatus) {
        ValueWrapper<List<PurchaseProductDTO>> productsForSupplier = purchaseService.getProductsByStatus(productStatus);
        return ResponseEntity.ok(productsForSupplier.getValue());
    }
}
