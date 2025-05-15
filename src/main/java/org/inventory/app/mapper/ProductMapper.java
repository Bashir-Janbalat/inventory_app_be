package org.inventory.app.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.ImageDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.*;
import org.inventory.app.repository.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class ProductMapper {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final AttributeRepository attributeRepository;
    private final StockRepository stockRepository;
    private final WarehouseRepository warehouseRepository;
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
        if (product.getStocks() != null && !product.getStocks().isEmpty()) {
            dto.setStocks(product.getStocks().stream().map(stockMapper::toDto).toList());
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
        if (dto.getStocks() != null && !dto.getStocks().isEmpty()) {
            setStocksFromDTO(product, dto);
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
        product.setProductAttributes(dto.getProductAttributes().stream().map(productAttributeMapper::toEntity)
                .peek(att -> att.setProduct(product)).toList());
    }

    private void setStocksFromDTO(Product product, ProductDTO dto) {
        List<Stock> stocks = dto.getStocks().stream()
                .map(stockMapper::toEntity)
                .peek(stock -> stock.setProduct(product))
                .collect(Collectors.toList());
        product.setStocks(stocks);
    }

    private void assignEntityRelations(Product product, ProductDTO dto) {
        Optional.ofNullable(dto.getCategoryID()).ifPresent(id -> product.setCategory(findCategoryById(id)));
        Optional.ofNullable(dto.getBrandID()).ifPresent(id -> product.setBrand(findBrandById(id)));
        Optional.ofNullable(dto.getSupplierID()).ifPresent(id -> product.setSupplier(findSupplierById(id)));
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

    private List<ImageDTO> mapImagesToDTO(Product product) {
        return product.getImages().stream().map(imageMapper::toDto).collect(Collectors.toList());
    }
}
