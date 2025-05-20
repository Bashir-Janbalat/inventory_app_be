package org.inventory.app.mapper;


import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PurchaseItemDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.Product;
import org.inventory.app.model.PurchaseItem;
import org.inventory.app.model.Warehouse;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.repository.WarehouseRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseItemMapper {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public PurchaseItemDTO toDto(PurchaseItem entity) {
        if (entity == null) return null;

        PurchaseItemDTO dto = new PurchaseItemDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setProductName(entity.getProduct().getName());
        dto.setSku(entity.getProduct().getSku());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setWarehouseId(entity.getWarehouse().getId());
        dto.setWarehouseName(entity.getWarehouse().getName());
        return dto;
    }

    public PurchaseItem toEntity(PurchaseItemDTO dto) {
        if (dto == null) return null;

        PurchaseItem item = new PurchaseItem();
        item.setId(dto.getId());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId()));
        item.setProduct(product);

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + dto.getWarehouseId()));
        item.setWarehouse(warehouse);

        return item;
    }
}