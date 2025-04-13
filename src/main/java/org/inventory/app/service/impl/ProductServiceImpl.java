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
        return productMapper.toDto(productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + id + "' not found.")));
    }

    public ProductDTO createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        setReferences(product, dto);
        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID '" + id + "' not found."));

        Product updated = productMapper.toEntity(dto);
        updated.setId(id); // wichtig!

        setReferences(updated, dto);

        Product saved = productRepository.save(updated);
        return productMapper.toDto(saved);
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with ID '" + id + "' not found.");
        }
        productRepository.deleteById(id);
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
        if (!dto.getImages().isEmpty()) {
            dto.getImages().forEach(image -> image.setProduct(product));
            product.setImages(dto.getImages());
        }
    }
}
