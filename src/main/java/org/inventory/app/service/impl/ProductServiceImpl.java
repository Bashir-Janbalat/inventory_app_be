package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.ProductMapper;
import org.inventory.app.model.Brand;
import org.inventory.app.model.Category;
import org.inventory.app.model.Product;
import org.inventory.app.model.Supplier;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.repository.SupplierRepository;
import org.inventory.app.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toDto).collect(Collectors.toList());
    }

    public ProductDTO getProductById(Long id) {
        return productMapper.toDto(productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found")));
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        setReferences(product, dto);
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Product updated = productMapper.toEntity(dto);
        updated.setId(id); // wichtig!

        setReferences(updated, dto);

        Product saved = productRepository.save(updated);
        return productMapper.toDto(saved);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private void setReferences(Product product, ProductDTO dto) {
        if (dto.getCategoryName() != null) {
            Category category = categoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new ResourceNotFoundException("Category '" + dto.getCategoryName() + "' not found"));
            product.setCategory(category);
        }

        if (dto.getBrandName() != null) {
            Brand brand = brandRepository.findByName(dto.getBrandName())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand '" + dto.getBrandName() + "' not found"));
            product.setBrand(brand);
        }

        if (dto.getSupplierName() != null) {
            Supplier supplier = supplierRepository.findByName(dto.getSupplierName())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier '" + dto.getSupplierName() + "' not found"));
            product.setSupplier(supplier);
        }
    }
}
