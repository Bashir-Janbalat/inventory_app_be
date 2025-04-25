package org.inventory.app.service;


import org.inventory.app.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductDTO> getAllProducts(Pageable pageable );
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO dto);
    ProductDTO updateProduct(Long id, ProductDTO dto);
    void deleteProduct(Long id);


}
