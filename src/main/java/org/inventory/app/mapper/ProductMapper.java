package org.inventory.app.mapper;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.*;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.repository.SupplierRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ProductMapper {


    private final StockMapper stockMapper;
    private final ImageMapper imageMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;

    public ProductDTO toDto(Product product) {
        if (product == null) return null;

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setImages(product.getImages().stream().map(imageMapper::toDto).toList());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getBrand() != null) {
            dto.setBrandName(product.getBrand().getName());
        }

        if (product.getSupplier() != null) {
            dto.setSupplierName(product.getSupplier().getName());
            dto.setSupplierContactEmail(product.getSupplier().getContactEmail());
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
            dto.setProductAttributes(product.getProductAttributes().stream().map(productAttributeMapper::toDto).toList());
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
        setReferences(product, dto);
        return product;
    }

    private void setReferences(Product product, ProductDTO dto) {
        if (dto.getCategoryID() != null) {
            Category category = categoryRepository.findById(dto.getCategoryID())
                    .orElseThrow(() -> new ResourceNotFoundException("Category with ID '" + dto.getCategoryID() + "' not found."));
            product.setCategory(category);
        }

        if (dto.getBrandID() != null) {
            Brand brand = brandRepository.findById(dto.getBrandID())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand with ID '" + dto.getBrandID() + "' not found."));
            product.setBrand(brand);
        }

        if (dto.getSupplierID() != null) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierID())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier with ID '" + dto.getSupplierID() + "' not found."));
            product.setSupplier(supplier);
        }
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            List<Image> images = new ArrayList<>();
            dto.getImages().forEach(imageDTO -> {
                Image imageMapperEntity = imageMapper.toEntity(imageDTO);
                imageMapperEntity.setProduct(product);
                images.add(imageMapperEntity);
            });
            product.setImages(images);
        }
        if (dto.getStock() != null) {
            Stock stockEntity = stockMapper.toEntity(dto.getStock());
            stockEntity.setProduct(product);
            product.setStock(stockEntity);
        }
        if (dto.getProductAttributes() != null && !dto.getProductAttributes().isEmpty()) {
            product.setProductAttributes(dto.getProductAttributes().stream().map(productAttributeDTO -> {
                ProductAttribute productAttribute = productAttributeMapper.toEntity(productAttributeDTO);
                productAttribute.setProduct(product);
                return productAttribute;
            }).toList());

        }
    }
}
