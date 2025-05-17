package org.inventory.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.enums.PurchaseStatus;
import org.inventory.app.service.PurchaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<PagedResponseDTO<PurchaseDTO>> getAllPurchases(Pageable pageable) {
        log.info("Request to get all purchases - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<PurchaseDTO> purchases = purchaseService.getAllPurchases(pageable);
        return ResponseEntity.ok(new PagedResponseDTO<>(purchases));
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
}
