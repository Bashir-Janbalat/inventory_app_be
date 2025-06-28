package org.inventory.app.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.ImageDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductAttributeDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;
import org.inventory.app.enums.ProductStatus;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.ImageMapper;
import org.inventory.app.mapper.ProductAttributeMapper;
import org.inventory.app.mapper.ProductMapper;
import org.inventory.app.model.*;
import org.inventory.app.repository.*;
import org.inventory.app.service.ProductService;
import org.inventory.app.service.StockMovementService;
import org.inventory.app.service.StockService;
import org.inventory.app.specification.ProductSpecifications;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageMapper imageMapper;
    private final StockMovementRepository stockMovementRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final StockService stockService;
    private final StockMovementService stockMovementService;
    private final ProductAttributeMapper productAttributeMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        log.info("Fetched {} products from DB (page {} size {}) (and cached in 'products')",
                productPage.getContent().size(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(productPage.map(productMapper::toDto));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "product", key = "#id")
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found.", id);
                    return new ResourceNotFoundException("Product with ID '" + id + "' not found.");
                });
        log.info("Fetched product with ID {} from DB (and cached in 'product')", id);
        return productMapper.toDto(product);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"products", "product", "searchProducts", "productCount", "featuredProducts", "relatedProducts"}, allEntries = true),
            @CacheEvict(value = {"statusProducts", "supplierProducts"}, allEntries = true),
            @CacheEvict(value = {"dashboardSummary", "productStatusSummary", "stockStatusSummary", "monthlyProductCount"}, allEntries = true),
    })
    public ProductDTO createProduct(ProductDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ProductDTO must not be null");
        }
        Product product = productMapper.toEntity(dto);
        Product savedProduct = productRepository.save(product);
        savedProduct.getStocks().forEach(stock -> {
            stockMovementService.createStockMovementFor(stock, stock.getQuantity(), MovementType.IN, MovementReason.CREATED);
            log.info("'{}' movement saved: '{} {}' units ,warehouse '{}', product '{}', username '{}'", MovementType.IN,
                    MovementReason.CREATED, stock.getQuantity(),
                    stock.getWarehouse().getName(), stock.getProduct().getName(), getCurrentUserName());
        });
        log.info("Created product with ID {}. Cache 'products','product','searchProducts','productCount','dashboardSummary','productStatusSummary','stockStatusSummary','monthlyProductCount' evicted.", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"products", "product", "searchProducts", "productCount", "featuredProducts", "relatedProducts"}, allEntries = true),
            @CacheEvict(value = {"statusProducts", "supplierProducts"}, allEntries = true),
            @CacheEvict(value = {"dashboardSummary", "productStatusSummary", "stockStatusSummary", "monthlyProductCount"}, allEntries = true),
    })
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to update non-existent product with ID {}.", id);
                    return new ResourceNotFoundException("Product with ID '" + id + "' not found.");
                });
        patchProductFromDTO(product, dto);
        Product saved = productRepository.save(product);

        log.info("Updated product with ID {}. Cache 'products','product','searchProducts','productCount','dashboardSummary','productStatusSummary','stockStatusSummary','monthlyProductCount' evicted.", id);
        return productMapper.toDto(saved);
    }

    public void patchProductFromDTO(Product product, ProductDTO dto) {
        if (dto == null || product == null) return;

        product.setName(dto.getName());
        product.setSku(dto.getSku());
        product.setDescription(dto.getDescription());
        product.setCostPrice(dto.getCostPrice());
        product.setSellingPrice(dto.getSellingPrice());
        assignEntityRelations(product, dto);

        if (dto.getImages() != null) {
            updateImagesFromDTO(product, dto);
        }
        if (dto.getProductAttributes() != null) {
            updateAttributesFromDTO(product, dto);
        }

        if (dto.getStocks() != null && !dto.getStocks().isEmpty()) {
            stockService.updateStocksFromDTO(product, dto);
        }
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
                ProductAttribute attribute = productAttributeMapper.toEntity(attributeDTO);
                attribute.setProduct(product);
                product.getProductAttributes().add(attribute);
                attributesToKeep.add(attribute);
            }
        }
        product.getProductAttributes().clear();
        product.getProductAttributes().addAll(attributesToKeep);
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"products", "product", "searchProducts", "productCount", "featuredProducts", "relatedProducts"}, allEntries = true),
            @CacheEvict(value = {"statusProducts", "supplierProducts"}, allEntries = true),
            @CacheEvict(value = {"dashboardSummary", "productStatusSummary", "stockStatusSummary", "monthlyProductCount"}, allEntries = true)
    })
    public void deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            log.warn("Attempted to delete non-existent product with ID {}.", id);
            throw new ResourceNotFoundException("Product with ID '" + id + "' not found.");
        }
        List<StockMovement> movements = stockMovementRepository.findByProductId(id);
        for (StockMovement movement : movements) {
            movement.setProductNameSnapshot(product.get().getName());
            movement.setProductDeleted(true);
            movement.setProduct(null);
        }
        stockMovementRepository.saveAll(movements);
        productRepository.deleteById(id);
        log.info("Deleted product with ID {}. Cache 'products','product','searchProducts','productCount','dashboardSummary','productStatusSummary','stockStatusSummary','monthlyProductCount' evicted.", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "searchProducts",
            key = "'search:' + #searchBy + ':categoryName:' + #categoryName + ':brandName:' + #brandName +" +
                  "':supplierName:' + #supplierName " +
                  "+ ':minPrice:' + #minPrice + ':maxPrice:' + #maxPrice " +
                  "+ ':sortBy:' + #sortBy + ':sortDirection:' + #sortDirection " +
                  "+ ':productStatus:' + #productStatus" +
                  "+ ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<ProductDTO> searchProducts(String searchBy, String categoryName,
                                                       String brandName, String supplierName, String sortDirection,
                                                       Integer minPrice, Integer maxPrice,
                                                       String sortBy, ProductStatus productStatus, Pageable pageable) {
        if (searchBy.isEmpty() && categoryName.isEmpty() && brandName.isEmpty() && supplierName.isEmpty()
            && productStatus == null && maxPrice == null && minPrice == null) {
            return getAllProducts(pageable);
        }

        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasNameLike(searchBy))
                .and(ProductSpecifications.hasCategory(categoryName))
                .and(ProductSpecifications.hasBrand(brandName))
                .and(ProductSpecifications.hasSupplier(supplierName))
                .and(ProductSpecifications.hasStatus(productStatus))
                .and(ProductSpecifications.hasPriceBetween(minPrice, maxPrice));

        Page<Product> result = productRepository.findAll(spec, pageable);
        log.info("Fetched {} products based on search filters from DB (page {} size {}) (and cached in searchProducts)",
                result.getContent().size(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(result.map(productMapper::toDto));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "featuredProducts", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public ValueWrapper<List<ProductDTO>> getFeaturedProducts(Pageable pageable) {
        List<Product> featured = productRepository.findByIsFeaturedTrue(pageable);
        return new ValueWrapper<>(featured.stream().map(productMapper::toDto).toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "relatedProducts", key = "'productId:' + #productId + ':limit:' + #limit + ':cat:' + #byCategory + ':brand:' + #byBrand")
    public ValueWrapper<List<ProductDTO>> getRelatedProducts(Long productId, int limit, boolean byCategory, boolean byBrand) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Pageable pageable = PageRequest.of(0, limit);
        Set<Product> relatedSet = new LinkedHashSet<>(product.getRelatedProducts());

        if (byCategory && relatedSet.size() < limit) {
            List<Product> byCategoryList = productRepository.findByCategoryAndIdNot(product.getCategory(), product.getId(), pageable);
            for (Product p : byCategoryList) {
                if (relatedSet.size() >= limit) break;
                relatedSet.add(p);
            }
        }

        if (byBrand && relatedSet.size() < limit) {
            List<Product> byBrandList = productRepository.findByBrandAndIdNot(product.getBrand(), product.getId(), pageable);
            for (Product p : byBrandList) {
                if (relatedSet.size() >= limit) break;
                relatedSet.add(p);
            }
        }

        List<ProductDTO> dtos = relatedSet.stream()
                .limit(limit)
                .map(productMapper::toDto)
                .toList();

        return new ValueWrapper<>(dtos);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "productCount")
    public ValueWrapper<Long> getTotalProductCount() {
        long count = productRepository.count();
        log.info("Fetched Product size: {} from DB (and cached in productCount)", count);
        return new ValueWrapper<>(count);
    }
}
