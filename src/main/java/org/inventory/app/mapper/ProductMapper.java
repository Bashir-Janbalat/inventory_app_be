package org.inventory.app.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.ImageDTO;
import org.inventory.app.dto.ProductAttributeDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.MovementType;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.*;
import org.inventory.app.repository.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class ProductMapper {


    private final StockMapper stockMapper;
    private final ImageMapper imageMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final AttributeRepository attributeRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;


    public ProductDTO toDto(Product product) {
        if (product == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setDescription(product.getDescription());
        dto.setCostPrice(product.getCostPrice());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setImages(mapImagesToDTO(product));
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
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        setReferences(product, dto);
        return product;
    }

    private void setReferences(Product product, ProductDTO dto) {
        assignEntityRelations(product, dto);

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            setImagesFromDTO(product, dto);
        }
        if (dto.getStock() != null) {
            setStockFromDTO(product, dto);
        }
        if (dto.getProductAttributes() != null && !dto.getProductAttributes().isEmpty()) {
            setAttributesFromDTO(product, dto);
        }
    }

    private void setImagesFromDTO(Product product, ProductDTO dto) {
        List<Image> images = new ArrayList<>();
        dto.getImages().forEach(imageDTO -> {
            Image imageMapperEntity = imageMapper.toEntity(imageDTO);
            imageMapperEntity.setProduct(product);
            images.add(imageMapperEntity);
        });
        product.setImages(images);
    }

    private void setAttributesFromDTO(Product product, ProductDTO dto) {
        product.setProductAttributes(dto.getProductAttributes().stream().map(productAttributeDTO -> {
            Attribute attribute = new Attribute();
            attribute.setName(productAttributeDTO.getAttributeName());
            Attribute saved = attributeRepository.save(attribute);
            ProductAttribute productAttribute = new ProductAttribute(product, saved, productAttributeDTO.getAttributeValue());
            productAttribute.setProduct(product);
            return productAttribute;
        }).toList());
    }

    private void setStockFromDTO(Product product, ProductDTO dto) {
        Stock stockEntity = stockMapper.toEntity(dto.getStock());
        stockEntity.setProduct(product);
        product.setStock(stockEntity);
    }

    private void assignEntityRelations(Product product, ProductDTO dto) {
        Optional.ofNullable(dto.getCategoryID()).ifPresent(id -> product.setCategory(findCategoryById(id)));
        Optional.ofNullable(dto.getBrandID()).ifPresent(id -> product.setBrand(findBrandById(id)));
        Optional.ofNullable(dto.getSupplierID()).ifPresent(id -> product.setSupplier(findSupplierById(id)));
    }

    private List<ImageDTO> mapImagesToDTO(Product product) {
        return product.getImages().stream().map(imageMapper::toDto).collect(Collectors.toList());
    }


    public void patchProductFromDTO(Product product, ProductDTO dto) {
        if (dto == null || product == null) return;

        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        assignEntityRelations(product, dto);
        if (dto.getStock() != null) {
            updateStockFromDTO(product, dto);
        }
        if (dto.getImages() != null) {
            updateImagesFromDTO(product, dto);
        }
        if (dto.getProductAttributes() != null) {
            updateAttributesFromDTO(product, dto);
        }
    }


    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category with id: " + id + " not found"));
    }

    private Brand findBrandById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand with ID '" + id + "' not found."));
    }

    private Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Supplier with ID '" + id + "' not found."));
    }

    private void updateStockFromDTO(Product product, ProductDTO dto) {
        Stock existingStock = product.getStock();
        Long currentWarehouseId = existingStock.getWarehouse().getId();
        Long newWarehouseId = dto.getStock().getWarehouse() != null ? dto.getStock().getWarehouse().getId() : null;
        MovementType type = MovementType.valueOf(dto.getStock().getMovementType());
        if (type == MovementType.TRANSFER) {
            Long destId = dto.getStock().getDestinationWarehouseId();
            if (destId == null || destId.equals(currentWarehouseId)) {
                throw new IllegalArgumentException("Destination warehouse must be different and not null.");
            }
            adjustDestinationStock(product, dto);
            existingStock.setQuantity(adjustSourceStockQuantity(product, dto));
            stockRepository.save(existingStock);
            return;
        }
        if (newWarehouseId != null && !newWarehouseId.equals(currentWarehouseId)) {
            stockRepository.delete(existingStock);

            Warehouse newWarehouse = warehouseRepository.findById(newWarehouseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + newWarehouseId));
            Stock newStock = new Stock();
            StockId newStockId = new StockId();
            newStockId.setProduct(product.getId());
            newStockId.setWarehouse(newWarehouse.getId());
            newStock.setProduct(product);
            newStock.setWarehouse(newWarehouse);
            newStock.setQuantity(adjustSourceStockQuantity(product, dto));
            product.setStock(newStock);
        } else {
            existingStock.setQuantity(adjustSourceStockQuantity(product, dto));
            stockRepository.save(existingStock);
        }
    }

    private int adjustSourceStockQuantity(Product product, ProductDTO dto) {
        int oldQuantity = product.getStock().getQuantity();
        int movementQuantity = dto.getStock().getMovementQuantity();
        String movementType = dto.getStock().getMovementType();

        MovementType type = MovementType.valueOf(movementType);

        return switch (type) {
            case IN, RETURN -> oldQuantity + movementQuantity;
            case OUT, DAMAGED -> oldQuantity - movementQuantity;
            case TRANSFER -> {
                Long destId = dto.getStock().getDestinationWarehouseId();
                if (destId == null || product.getStock().getWarehouse().getId().equals(destId)) {
                    throw new IllegalArgumentException("Invalid destination warehouse");
                }
                yield oldQuantity - movementQuantity;
            }
            default -> throw new IllegalStateException("Unexpected movement type: " + movementType);
        };
    }

    private void adjustDestinationStock(Product product, ProductDTO dto) {
        if (!MovementType.valueOf(dto.getStock().getMovementType()).equals(MovementType.TRANSFER)) {
            return;
        }
        Long newWarehouseId = dto.getStock().getDestinationWarehouseId();
        if (newWarehouseId == null) {
            throw new IllegalArgumentException("Destination warehouse ID must not be null");
        }

        Warehouse destWarehouse = warehouseRepository.findById(newWarehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + newWarehouseId));

        Stock destinationStock = stockRepository.findByProductAndWarehouse(product, destWarehouse)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setProduct(product);
                    newStock.setWarehouse(destWarehouse);
                    newStock.setQuantity(0); // initialer Wert
                    return newStock;
                });

        destinationStock.setQuantity(destinationStock.getQuantity() + dto.getStock().getMovementQuantity());
        stockRepository.save(destinationStock);
    }

    private void updateImagesFromDTO(Product product, ProductDTO dto) {
        Map<Long, Image> existingImages = product.getImages().stream().filter(image -> image.getId() != null).collect(Collectors.toMap(Image::getId, image -> image));
        List<Image> imagesToKeep = new ArrayList<>();

        for (ImageDTO imageDTO : dto.getImages()) {
            if (imageDTO.getId() != null && existingImages.containsKey(imageDTO.getId())) {
                Image existingImage = existingImages.get(imageDTO.getId());
                existingImage.setImageUrl(imageDTO.getImageUrl());
                existingImage.setAltText(imageDTO.getAltText());
                imagesToKeep.add(existingImage);
            } else {
                Image newImage = imageMapper.toEntity(imageDTO);
                newImage.setProduct(product);
                imagesToKeep.add(newImage);
            }
        }
        product.getImages().clear();
        product.getImages().addAll(imagesToKeep);
    }

    private void updateAttributesFromDTO(Product product, ProductDTO dto) {
        Map<ProductAttributeId, ProductAttribute> existingAttributes = product.getProductAttributes().stream()
                .collect(Collectors.toMap(
                        attr -> new ProductAttributeId(attr.getProduct().getId(), attr.getAttribute().getId()),
                        attr -> attr
                ));
        List<ProductAttribute> attributesToKeep = new ArrayList<>();
        for (ProductAttributeDTO attributeDTO : dto.getProductAttributes()) {
            ProductAttributeId id = new ProductAttributeId(product.getId(), attributeDTO.getAttributeID());
            if (attributeDTO.getAttributeID() != null && existingAttributes.containsKey(id)) {
                ProductAttribute existingAttr = existingAttributes.get(id);
                existingAttr.setValue(attributeDTO.getAttributeValue());
                attributesToKeep.add(existingAttr);
            } else {
                Attribute attribute = attributeRepository.findFirstByName(attributeDTO.getAttributeName()).orElseGet(
                        () -> attributeRepository.save(new Attribute(attributeDTO.getAttributeName()))
                );
                ProductAttribute newProductAttribute = new ProductAttribute(product, attribute, attributeDTO.getAttributeValue());
                product.getProductAttributes().add(newProductAttribute);
                attributesToKeep.add(newProductAttribute);
            }
        }
        product.getProductAttributes().clear();
        product.getProductAttributes().addAll(attributesToKeep);
    }
}
