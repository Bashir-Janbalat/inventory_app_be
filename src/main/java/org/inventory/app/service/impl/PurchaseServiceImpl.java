package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.enums.ProductStatus;
import org.inventory.app.enums.PurchaseStatus;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.PurchaseMapper;
import org.inventory.app.model.*;
import org.inventory.app.projection.PurchaseProductDTO;
import org.inventory.app.repository.PurchaseRepository;
import org.inventory.app.repository.StockRepository;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.PurchaseItemService;
import org.inventory.app.service.PurchaseService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseMapper purchaseMapper;
    private final PurchaseItemService purchaseItemService;
    private final StockRepository stockRepository;


    @Override
    @Transactional
    @CacheEvict(value = {"purchases", "purchase", "supplierProducts", "statusProducts"}, allEntries = true)
    public PurchaseDTO createPurchase(PurchaseDTO purchaseDTO) {
        if (purchaseDTO.getSupplierId() == null) {
            throw new IllegalArgumentException("supplierId is null");
        }
        Supplier supplier = supplierRepository.findById(purchaseDTO.getSupplierId()).orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        Purchase purchase = new Purchase();
        purchase.setSupplier(supplier);
        purchase.setStatus(PurchaseStatus.PENDING);
        Purchase saved = purchaseRepository.save(purchase);

        List<PurchaseItem> items = purchaseItemService.savePurchaseItems(purchaseDTO.getItems(), saved);
        saved.setItems(items);

        PurchaseDTO dto = purchaseMapper.toDto(purchaseRepository.save(saved));
        log.info("Created a new purchase with ID {} Cache 'purchases','purchase','supplierProducts','statusProducts' evicted", dto.getId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "purchases")
    public PagedResponseDTO<PurchaseDTO> getAllPurchases(Pageable pageable, LocalDate date) {
        Page<Purchase> purchases;
        if (date != null) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();
            purchases = purchaseRepository.findByCreatedAtBetween(startOfDay, startOfNextDay, pageable);
        } else {
            purchases = purchaseRepository.findAll(pageable);
        }

        log.info("Fetched {} purchases (page {} size {}) from DB (and cached in 'purchases')", purchases.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(purchases.map(purchaseMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "purchase", key = "#id")
    public PurchaseDTO getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        log.info("Fetched purchase with ID {} from DB (and cached in 'purchase')", id);
        return purchaseMapper.toDto(purchase);
    }

    @Override
    @Transactional

    @Caching(evict = {
            @CacheEvict(value = {"products", "product", "searchProducts"}, allEntries = true),
            @CacheEvict(value = {"warehouses", "pagedWarehouses", "warehous", "warehousesStats"}, allEntries = true),
            @CacheEvict(value = "purchase", key = "#id"),
            @CacheEvict(value = {"purchases", "supplierProducts", "statusProducts",}, allEntries = true)
    })
    public PurchaseDTO updatePurchaseStatus(Long id, PurchaseStatus status) {
        if (status != PurchaseStatus.COMPLETED && status != PurchaseStatus.CANCELLED) {
            throw new IllegalArgumentException("Invalid status value");
        }
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        for (PurchaseItem item : purchase.getItems()) {
            Product product = item.getProduct();
            if (status == PurchaseStatus.CANCELLED) {
                product.setProductStatus(ProductStatus.DELETED);
                continue;
            }
            product.setSupplier(purchase.getSupplier());
            Warehouse warehouse = item.getWarehouse();
            int quantity = item.getQuantity();
            product.setProductStatus(ProductStatus.ACTIVE);
            updateStocks(product, warehouse, quantity);

        }
        purchase.setStatus(PurchaseStatus.COMPLETED);
        Purchase updated = purchaseRepository.save(purchase);
        log.info("Updated purchase status successfully for id: {} Cache 'purchases','purchase','supplierProducts','statusProducts' evicted", id);
        return purchaseMapper.toDto(updated);
    }

    private void updateStocks(Product product, Warehouse warehouse, int quantity) {
        Optional<Stock> existingStock = stockRepository.findByProductAndWarehouse(product, warehouse);
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.setQuantity(stock.getQuantity() + quantity);
            stockRepository.save(stock);
        } else {
            Stock newStock = new Stock();
            newStock.setProduct(product);
            newStock.setWarehouse(warehouse);
            newStock.setQuantity(quantity);
            stockRepository.save(newStock);
        }
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "supplierProducts", key = "#supplierId")
    public ValueWrapper<List<PurchaseProductDTO>> getProductsForSupplier(Long supplierId) {
        List<PurchaseProductDTO> purchaseProductDTOS = purchaseRepository.getProductsForSupplier(supplierId);
        return new ValueWrapper<>(purchaseProductDTOS);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "statusProducts", key = "#productStatus")
    public ValueWrapper<List<PurchaseProductDTO>> getProductsByStatus(ProductStatus productStatus) {
        List<PurchaseProductDTO> purchaseProductDTOS = purchaseRepository.getProductsByStatus(productStatus);
        return new ValueWrapper<>(purchaseProductDTOS);
    }
}
