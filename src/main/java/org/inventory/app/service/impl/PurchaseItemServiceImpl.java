package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.PurchaseItemDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.Product;
import org.inventory.app.model.Purchase;
import org.inventory.app.model.PurchaseItem;
import org.inventory.app.model.Warehouse;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.repository.PurchaseItemRepository;
import org.inventory.app.repository.WarehouseRepository;
import org.inventory.app.service.PurchaseItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseItemServiceImpl implements PurchaseItemService {

    private final PurchaseItemRepository purchaseItemRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public List<PurchaseItem> savePurchaseItems(List<PurchaseItemDTO> itemDTOs, Purchase purchase) {
        List<PurchaseItem> items = new ArrayList<>();
        for (PurchaseItemDTO itemDTO : itemDTOs) {
            if (itemDTO.getProductId() == null || itemDTO.getWarehouseId() == null) {
                throw new IllegalArgumentException("Product or warehouse id is null");
            }
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + itemDTO.getProductId()));
            Warehouse warehouse = warehouseRepository.findById(itemDTO.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id " + itemDTO.getWarehouseId()));

            PurchaseItem item = new PurchaseItem();
            item.setPurchase(purchase);
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getUnitPrice());
            item.setWarehouse(warehouse);
            items.add(item);
        }
        return purchaseItemRepository.saveAll(items);
    }
}


