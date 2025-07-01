package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.common.ProductSearchFilter;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductDTO;
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
            description = "Search products by name, category, brand, supplier, status, stock, and price with pagination and sorting")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductDTO>> getAllProducts(
            @ModelAttribute ProductSearchFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Sort sort = Sort.by(Sort.Direction.fromString(filter.getSortDirection()), filter.getSortBy());

        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponseDTO<ProductDTO> products = productService.searchProducts(filter, pageable);
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
    public ResponseEntity<PagedResponseDTO<ProductDTO>> getFeaturedProducts(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponseDTO<ProductDTO> result = productService.getFeaturedProducts(pageable);
        return ResponseEntity.ok(result);
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