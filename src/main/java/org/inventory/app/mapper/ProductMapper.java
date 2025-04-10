package org.inventory.app.mapper;

import org.inventory.app.dto.ProductDTO;
import org.inventory.app.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDto(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getBrand() != null) {
            dto.setBrandName(product.getBrand().getName());
        }

        if (product.getSupplier() != null) {
            dto.setSupplierName(product.getSupplier().getName());
        }

        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) return null;

        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        // TODO Achtung: category, brand und supplier müssen separat gesetzt werden!
        // zB. per Service oder Repository nach ID/Name laden

        return product;
    }
}
