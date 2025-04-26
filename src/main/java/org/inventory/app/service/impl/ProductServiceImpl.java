package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.ProductMapper;
import org.inventory.app.model.Product;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.service.ProductService;
import org.inventory.app.specification.ProductSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(productMapper::toDto);
    }

    public ProductDTO getProductById(Long id) {
        return productMapper.toDto(productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + id + "' not found.")));
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + id + "' not found."));

        Product updated = productMapper.toEntity(dto);
        updated.setId(id); // wichtig!


        Product saved = productRepository.save(updated);
        return productMapper.toDto(saved);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with ID '" + id + "' not found.");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDTO> searchProducts(String searchBy, String categoryName, String brandName, String supplierName, Pageable pageable) {

        if (searchBy.isEmpty() && categoryName.isEmpty() && brandName.isEmpty() && supplierName.isEmpty()) {
            return getAllProducts(pageable);
        }
        Specification<Product> spec = Specification
                .where(ProductSpecifications.hasNameLike(searchBy))
                .and(ProductSpecifications.hasCategory(categoryName))
                .and(ProductSpecifications.hasBrand(brandName))
                .and(ProductSpecifications.hasSupplier(supplierName));

        return productRepository.findAll(spec, pageable).map(productMapper::toDto);
    }


}
