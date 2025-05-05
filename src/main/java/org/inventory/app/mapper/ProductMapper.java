package org.inventory.app.mapper;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.ImageDTO;
import org.inventory.app.dto.ProductAttributeDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.*;
import org.inventory.app.repository.AttributeRepository;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.repository.SupplierRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ProductMapper {


    private final StockMapper stockMapper;
    private final ImageMapper imageMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final AttributeRepository attributeRepository;

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
            dto.setCategoryID(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getBrand() != null) {
            dto.setBrandID(product.getBrand().getId());
            dto.setBrandName(product.getBrand().getName());
        }

        if (product.getSupplier() != null) {
            dto.setSupplierID(product.getSupplier().getId());
            dto.setSupplierName(product.getSupplier().getName());
            dto.setSupplierContactEmail(product.getSupplier().getContactEmail());
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

    public void patchProductFromDTO(Product product, ProductDTO dto) {
        if (dto == null || product == null)
            return;

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getSku() != null) {
            product.setSku(dto.getSku());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getCategoryID() != null) {
            categoryRepository.findById(dto.getCategoryID()).ifPresent(product::setCategory);
        }
        if (dto.getBrandID() != null) {
            brandRepository.findById(dto.getBrandID()).ifPresent(product::setBrand);
        }
        if (dto.getSupplierID() != null) {
            supplierRepository.findById(dto.getSupplierID()).ifPresent(product::setSupplier);
        }
        if (dto.getImages() != null) {
            Map<Long, Image> existingImages = product.getImages().stream()
                    .filter(image -> image.getId() != null)
                    .collect(Collectors.toMap(Image::getId, image -> image));

            List<Image> updatedImages = new ArrayList<>();

            for (ImageDTO imageDTO : dto.getImages()) {
                if (imageDTO.getId() != null && existingImages.containsKey(imageDTO.getId())) {
                    Image existingImage = existingImages.get(imageDTO.getId());
                    existingImage.setImageUrl(imageDTO.getImageUrl());
                    existingImage.setAltText(imageDTO.getAltText());
                    updatedImages.add(existingImage);
                } else {
                    Image newImage = imageMapper.toEntity(imageDTO);
                    newImage.setProduct(product);
                    updatedImages.add(newImage);
                }
            }
            product.getImages().clear();
            product.getImages().addAll(updatedImages);
        }
        if (dto.getStock() != null) {
            if (product.getStock() != null) {
                Stock existingStock = product.getStock();
                existingStock.setQuantity(dto.getStock().getQuantity());
                existingStock.setWarehouseLocation(dto.getStock().getWarehouseLocation());
            } else {
                Stock newStock = stockMapper.toEntity(dto.getStock());
                newStock.setProduct(product);
                product.setStock(newStock);
            }
        }
        if (dto.getProductAttributes() != null) {
            Map<ProductAttributeId, ProductAttribute> existingAttributes = product.getProductAttributes().stream()
                    .collect(Collectors.toMap(attr -> new ProductAttributeId(attr.getProduct().getId(), attr.getAttribute().getId()), attr -> attr));

            List<ProductAttribute> updatedAttributes = new ArrayList<>();

            for (ProductAttributeDTO attributeDTO : dto.getProductAttributes()) {
                ProductAttributeId attributeId = new ProductAttributeId(dto.getId(), attributeDTO.getAttributeID());
                if (attributeDTO.getAttributeID() != null && existingAttributes.containsKey(attributeId)) {
                    ProductAttribute existingAttr = existingAttributes.get(attributeId);
                    existingAttr.setValue(attributeDTO.getAttributeValue());
                    updatedAttributes.add(existingAttr);
                } else {
                    if (attributeDTO.getAttributeID() == null || !attributeRepository.existsById(attributeDTO.getAttributeID())) {
                        throw new ResourceNotFoundException("Attribute with ID '" + attributeDTO.getAttributeID() + "' not found.");
                    }
                    Attribute attribute = attributeRepository.findById(attributeDTO.getAttributeID()).get();
                    ProductAttribute newAttr = new ProductAttribute(product, attribute, attributeDTO.getAttributeValue());
                    updatedAttributes.add(newAttr);
                }
            }
            product.getProductAttributes().clear();
            product.getProductAttributes().addAll(updatedAttributes);
        }
    }
}
