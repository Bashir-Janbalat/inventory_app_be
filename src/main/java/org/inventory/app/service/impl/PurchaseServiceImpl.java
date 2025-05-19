package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.enums.PurchaseStatus;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.PurchaseMapper;
import org.inventory.app.model.Purchase;
import org.inventory.app.model.PurchaseItem;
import org.inventory.app.model.Supplier;
import org.inventory.app.projection.PurchaseProductDTO;
import org.inventory.app.repository.PurchaseRepository;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.PurchaseItemService;
import org.inventory.app.service.PurchaseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseMapper purchaseMapper;
    private final PurchaseItemService purchaseItemService;


    @Override
    @Transactional
    @CacheEvict(value = {"purchases", "purchase", "purchaseProducts"}, allEntries = true)
    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        Supplier supplier = supplierRepository.findById(purchaseDTO.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setStatus(PurchaseStatus.PENDING);
        Purchase saved = purchaseRepository.save(purchase);

        List<PurchaseItem> items = purchaseItemService.savePurchaseItems(purchaseDTO.getItems(), saved);
        saved.setItems(items);

        PurchaseDTO dto = purchaseMapper.toDto(purchaseRepository.save(saved));
        log.info("Created a new purchase with ID {} Cache 'purchases','purchase' evicted", dto.getId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "purchases")
    public PagedResponseDTO<PurchaseDTO> getAllPurchases(Pageable pageable) {
        Page<Purchase> purchases = purchaseRepository.findAll(pageable);
        log.info("Fetched {} purchases (page {} size {}) from DB (and cached in 'purchases')",
                purchases.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(purchases.map(purchaseMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "purchase", key = "#id")
    public PurchaseDTO getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        log.info("Fetched purchase with ID {} from DB (and cached in 'purchase')", id);
        return purchaseMapper.toDto(purchase);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"purchases", "purchase", "purchaseProducts"}, allEntries = true)
    public PurchaseDTO updatePurchaseStatus(Long id, PurchaseStatus status) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        purchase.setStatus(status);
        Purchase updated = purchaseRepository.save(purchase);
        log.info("Updated purchase status successfully for id: {} (and cached in 'purchase')", id);
        return purchaseMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "purchaseProducts")
    public ValueWrapper<List<PurchaseProductDTO>> getProductsForSupplier(Long supplierId) {
        List<PurchaseProductDTO> purchaseProductDTOS = purchaseRepository.getProductsForSupplier(supplierId);
        return new ValueWrapper<>(purchaseProductDTOS);
    }
}
