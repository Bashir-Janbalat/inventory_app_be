package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.enums.PurchaseStatus;
import org.inventory.app.projection.PurchaseProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseService {

    PurchaseDTO createPurchase(PurchaseDTO dto);

    PagedResponseDTO<PurchaseDTO> getAllPurchases(Pageable pageable);

    PurchaseDTO getPurchaseById(Long id);

    PurchaseDTO updatePurchaseStatus(Long id, PurchaseStatus status);

    ValueWrapper<List<PurchaseProductDTO>> getProductsForSupplier(Long supplierId);

}
