package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.service.BrandService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "Operations related to product brands")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "Create a new brand", description = "Create a new product brand")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BrandDTO> createBrand(@RequestBody @Valid BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
    }

    @Operation(summary = "Get paginated list of brands", description = "Retrieve a paginated list of brands with sorting")
    @GetMapping
    public ResponseEntity<PagedResponseDTO<BrandDTO>> getAllBrands(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam(required = false) String searchByCategory) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ?
                Sort.by("name").descending() :
                Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<BrandDTO> brands = brandService.getAllBrands(searchByCategory,pageable);
        return ResponseEntity.ok(brands);
    }

    @Operation(summary = "Get brand by ID", description = "Retrieve a brand's details by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    @Operation(summary = "Update brand by ID", description = "Update an existing brand's details by its ID")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        return ResponseEntity.ok(brandService.updateBrand(id, brandDTO));
    }

    @Operation(summary = "Get total number of brands", description = "Get the total count of all brands")
    @GetMapping("/brand-size")
    public ResponseEntity<Long> getTotalBrandCount() {
        ValueWrapper<Long> count = brandService.getTotalBrandCount();
        return ResponseEntity.ok(count.getValue());
    }

    @Operation(summary = "Delete brand by ID", description = "Delete a brand by its ID (ADMIN role required)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

}
