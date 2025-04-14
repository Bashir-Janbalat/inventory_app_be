package org.inventory.app.mapper;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.model.Product;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductMapper {

    private final StockMapper stockMapper;
    private final ImageMapper imageMapper;
    private final ProductAttributeMapper productAttributeMapper;

    public ProductDTO toDto(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImages(product.getImages().stream().
                map(imageMapper::toDto).toList());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getBrand() != null) {
            dto.setBrandName(product.getBrand().getName());
        }

        if (product.getSupplier() != null) {
            dto.setSupplierName(product.getSupplier().getName());
        }
        if (product.getCategory() != null) {
            dto.setCategoryID(product.getCategory().getId());
        }

        if (product.getBrand() != null) {
            dto.setBrandID(product.getBrand().getId());
        }

        if (product.getSupplier() != null) {
            dto.setSupplierID(product.getSupplier().getId());
        }
        if (product.getStock() != null) {
            dto.setStock(stockMapper.toDto(product.getStock()));
        }
        if (!product.getProductAttributes().isEmpty()) {
            dto.setProductAttributes(product.getProductAttributes().stream().
                    map(productAttributeMapper::toDto).toList());
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


        return product;
    }
}
