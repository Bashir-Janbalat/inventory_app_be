package org.inventory.app.service;


import org.inventory.app.common.ProductSearchFilter;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.ProductDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    PagedResponseDTO<ProductDTO> getAllProducts(Pageable pageable);

    ProductDTO getProductById(Long id);

    ProductDTO createProduct(ProductDTO dto);

    ProductDTO updateProduct(Long id, ProductDTO dto);

    void deleteProduct(Long id);

    ValueWrapper<Long> getTotalProductCount();

    PagedResponseDTO<ProductDTO> searchProducts(ProductSearchFilter filter, Pageable pageable);

    PagedResponseDTO<ProductDTO> getFeaturedProducts(Pageable pageable);

    ValueWrapper<List<ProductDTO>> getRelatedProducts(Long productId, int limit, boolean byCategory, boolean byBrand);
}
