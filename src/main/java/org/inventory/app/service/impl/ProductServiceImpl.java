package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.ProductMapper;
import org.inventory.app.model.Product;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.service.ProductService;
import org.inventory.app.specification.ProductSpecifications;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        log.info("Fetched {} products from DB (cached the page)", productPage.getTotalElements());
        return productPage.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product with ID {} not found.", id);
                    return new ResourceNotFoundException("Product with ID '" + id + "' not found.");
                });
        log.info("Fetched product with ID {} (cached)", id);
        return productMapper.toDto(product);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        log.info("Created product with ID {}. Cache 'products' evicted.", saved.getId());
        return productMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempted to update non-existent product with ID {}.", id);
                    return new ResourceNotFoundException("Product with ID '" + id + "' not found.");
                });

        Product updated = productMapper.toEntity(dto);
        updated.setId(id);
        Product saved = productRepository.save(updated);
        log.info("Updated product with ID {}. Cache 'products' evicted.", id);
        return productMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent product with ID {}.", id);
            throw new ResourceNotFoundException("Product with ID '" + id + "' not found.");
        }
        productRepository.deleteById(id);
        log.info("Deleted product with ID {}. Cache 'products' evicted.", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "'search:' + #searchBy + ':' + #categoryName + ':' + #brandName + ':' + #supplierName + ':page:' + #pageable.pageNumber")
    public Page<ProductDTO> searchProducts(String searchBy, String categoryName, String brandName, String supplierName, Pageable pageable) {
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
        log.info("Fetched {} products based on search filters (cached)", result.getTotalElements());
        return result.map(productMapper::toDto);
    }
}
