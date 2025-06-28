package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.ProductStatus;
import org.inventory.app.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Operations related to products management")
public class ProductController {


    private final ProductService productService;

    @Operation(summary = "Get paginated list of products with filtering and sorting",
            description = "Search products by name, category, brand, supplier and status with pagination and sorting")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "") String searchBy,
            @RequestParam(defaultValue = "") String categoryName,
            @RequestParam(defaultValue = "") String brandName,
            @RequestParam(defaultValue = "") String supplierName,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) ProductStatus productStatus) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<ProductDTO> products = productService.searchProducts(
                searchBy,
                categoryName,
                brandName,
                supplierName,
                sortDirection,
                minPrice,
                maxPrice,
                sortBy,
                productStatus,
                pageable
        );
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Get product details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO dto) {
        ProductDTO createdProduct = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @Operation(summary = "Update an existing product (ADMIN role required)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @Operation(summary = "Delete a product by ID (ADMIN role required)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get total count of products")
    @GetMapping("/product-size")
    public ResponseEntity<Long> getProductSize() {
        ValueWrapper<Long> productCount = productService.getTotalProductCount();
        return ResponseEntity.ok(productCount.getValue());
    }

    @Operation(summary = "Get featured products")
    @GetMapping("/featured")
    public ResponseEntity<List<ProductDTO>> getFeaturedProducts(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getFeaturedProducts(pageable).getValue());
    }

    @Operation(summary = "Get related products for a specific product")
    @GetMapping("/{productId}/related")
    public ResponseEntity<List<ProductDTO>> getRelatedProducts(@PathVariable Long productId,
                                                               @RequestParam(defaultValue = "5") int limit,
                                                               @RequestParam(defaultValue = "true") boolean byCategory,
                                                               @RequestParam(defaultValue = "true") boolean byBrand) {
        int safeLimit = Math.min(limit, 20);
        return ResponseEntity.ok(productService.getRelatedProducts(productId, safeLimit, byCategory, byBrand).getValue());
    }
}