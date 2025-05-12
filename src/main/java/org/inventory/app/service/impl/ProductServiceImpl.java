package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.MovementReason;
import org.inventory.app.enums.MovementType;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.ProductMapper;
import org.inventory.app.model.Product;
import org.inventory.app.model.StockMovement;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.repository.StockMovementRepository;
import org.inventory.app.service.ProductService;
import org.inventory.app.specification.ProductSpecifications;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final StockMovementRepository stockMovementRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        log.info("Fetched {} products from DB (and cached in 'products')", productPage.getTotalElements());
        return productPage.map(productMapper::toDto);
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
    @CacheEvict(value = {"products", "product", "searchProducts", "productCount"}, allEntries = true)
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product savedProduct = productRepository.save(product);
        StockMovement movement = buildStockMovementFromDTO(savedProduct, dto);
        stockMovementRepository.save(movement);
        log.info("Created product with ID {}. Cache 'products','product','searchProducts','productCount' evicted.", savedProduct.getId());
        return productMapper.toDto(savedProduct);
    }

    private StockMovement buildStockMovementFromDTO(Product product, ProductDTO dto) {
        return StockMovement.builder()
                .product(product)
                .warehouseId(dto.getStock().getWarehouse().getId())
                .quantity(dto.getStock().getQuantity())
                .movementType(MovementType.IN)
                .reason(MovementReason.CREATED)
                .username(getCurrentUserName())
                .build();
    }

    private String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }

    @Transactional
    @CacheEvict(value = {"products", "product", "searchProducts", "productCount"}, allEntries = true)
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to update non-existent product with ID {}.", id);
                    return new ResourceNotFoundException("Product with ID '" + id + "' not found.");
                });

        if (dto.getStock() != null && dto.getStock().getMovementQuantity() != null && dto.getStock().getMovementType() != null) {
            int oldQuantity = product.getStock() != null ? product.getStock().getQuantity() : 0;
            int inputQuantity = dto.getStock().getMovementQuantity();
            MovementType movementType = MovementType.valueOf(dto.getStock().getMovementType());

            if (inputQuantity <= 0) {
                throw new IllegalArgumentException("Movement quantity must be provided and greater than zero.");
            }

            if (!isStockMovementValid(oldQuantity, inputQuantity, movementType)) {
                throw new IllegalArgumentException("Invalid quantity for movement type: " + movementType);
            }

            createStockUpdateMovement(product, inputQuantity, movementType)
                    .ifPresent(stockMovementRepository::save);
        }

        productMapper.patchProductFromDTO(product, dto);
        Product saved = productRepository.save(product);

        log.info("Updated product with ID {}. Cache 'products','product','searchProducts','productCount' evicted.", id);
        return productMapper.toDto(saved);
    }

    private Optional<StockMovement> createStockUpdateMovement(Product product, int quantity, MovementType type) {
        if (quantity == 0) return Optional.empty();

        return Optional.of(
                StockMovement.builder()
                        .product(product)
                        .warehouseId(product.getStock().getWarehouse().getId())
                        .quantity(quantity)
                        .movementType(type)
                        .reason(mapDefaultReason(type))
                        .username(getCurrentUserName())
                        .build()
        );
    }

    private boolean isStockMovementValid(int oldQuantity, int inputQuantity, MovementType type) {
        return switch (type) {
            case IN, RETURN -> (oldQuantity + inputQuantity) > oldQuantity;
            case OUT, TRANSFER, DAMAGED -> (oldQuantity - inputQuantity) >= 0;
        };
    }

    private MovementReason mapDefaultReason(MovementType type) {
        return switch (type) {
            case TRANSFER -> MovementReason.TRANSFERRED;
            case RETURN -> MovementReason.RETURNED;
            case DAMAGED -> MovementReason.DAMAGED;
            default -> MovementReason.UPDATED;
        };
    }


    @Transactional
    @CacheEvict(value = {"products", "product", "searchProducts", "productCount"}, allEntries = true)
    public void deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            log.warn("Attempted to delete non-existent product with ID {}.", id);
            throw new ResourceNotFoundException("Product with ID '" + id + "' not found.");
        }
        productRepository.deleteById(id);
        log.info("Deleted product with ID {}. Cache 'products','product','searchProducts','productCount' evicted.", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "searchProducts",
            key = "'search:' + #searchBy + ':' + #categoryName + ':' + #brandName + ':' + #supplierName " +
                    "+ ':sortBy:' + #sortBy + ':sortDirection:' + #sortDirection " +
                    "+ ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<ProductDTO> searchProducts(String searchBy, String categoryName,
                                           String brandName, String supplierName, String sortDirection,
                                           String sortBy, Pageable pageable) {
        if (searchBy.isEmpty() && categoryName.isEmpty() && brandName.isEmpty() && supplierName.isEmpty()) {
            log.info("Empty search parameters - fetching all products.");
            return getAllProducts(pageable);
        }

        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasNameLike(searchBy))
                .and(ProductSpecifications.hasCategory(categoryName))
                .and(ProductSpecifications.hasBrand(brandName))
                .and(ProductSpecifications.hasSupplier(supplierName));

        Page<Product> result = productRepository.findAll(spec, pageable);
        log.info("Fetched {} products based on search filters from DB (and cached in searchProducts)", result.getTotalElements());
        return result.map(productMapper::toDto);
    }

    @Override
    @Cacheable(value = "productCount")
    public Long getTotalProductCount() {
        long count = productRepository.count();
        log.info("Fetched Product size: {} from DB (and cached in productCount)", count);
        return count;
    }
}
