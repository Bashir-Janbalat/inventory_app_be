package org.inventory.app.service;

import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.enums.PurchaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseService {

    PurchaseDTO createPurchase(PurchaseDTO dto);

    Page<PurchaseDTO> getAllPurchases(Pageable pageable);

    PurchaseDTO getPurchaseById(Long id);

    PurchaseDTO updatePurchaseStatus(Long id, PurchaseStatus status);
}
