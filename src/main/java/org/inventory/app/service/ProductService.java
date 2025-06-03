package org.inventory.app.service;


import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductDTO;
import org.inventory.app.enums.ProductStatus;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    PagedResponseDTO<ProductDTO> getAllProducts(Pageable pageable );
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO dto);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    void deleteProduct(Long id);
    ValueWrapper<Long> getTotalProductCount();

    PagedResponseDTO<ProductDTO> searchProducts(String searchBy, String categoryName,
                                                String brandName, String supplierName, String sortDirection,
                                                Integer minPrice, Integer maxPrice,
                                                String sortBy, ProductStatus productStatus , Pageable pageable);
}
