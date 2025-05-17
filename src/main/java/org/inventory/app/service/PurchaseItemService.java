package org.inventory.app.service;

import org.inventory.app.dto.PurchaseItemDTO;
import org.inventory.app.model.Purchase;
import org.inventory.app.model.PurchaseItem;

import java.util.List;

public interface PurchaseItemService {

    List<PurchaseItem>  savePurchaseItems(List<PurchaseItemDTO> itemDTOs, Purchase purchase);
}
