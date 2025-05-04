package org.inventory.app.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {


    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PagedResponseDTO<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(defaultValue = "") String searchBy,
            @RequestParam(defaultValue = "") String categoryName,
            @RequestParam(defaultValue = "") String brandName,
            @RequestParam(defaultValue = "") String supplierName) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductDTO> products = productService.searchProducts(
                searchBy,
                categoryName,
                brandName,
                supplierName,
                pageable
        );
        return ResponseEntity.ok(new PagedResponseDTO<>(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO dto) {
        ProductDTO createdProduct = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product-size")
    public ResponseEntity<Long> getProductSize() {
        Long productCount = productService.getTotalProductCount();
        return ResponseEntity.ok(productCount);
    }
}