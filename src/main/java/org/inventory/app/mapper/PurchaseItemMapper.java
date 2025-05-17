package org.inventory.app.mapper;


import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PurchaseItemDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.Product;
import org.inventory.app.model.PurchaseItem;
import org.inventory.app.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurchaseItemMapper {

    private final ProductRepository productRepository;

    public PurchaseItemDTO toDto(PurchaseItem entity) {
        if (entity == null) return null;

        PurchaseItemDTO dto = new PurchaseItemDTO();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct().getId());
        dto.setProductName(entity.getProduct().getName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
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

        return item;
    }
}