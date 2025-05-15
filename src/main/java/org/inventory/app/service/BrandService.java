package org.inventory.app.service;

import org.inventory.app.dto.BrandDTO;
import org.inventory.app.dto.BrandProductCountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrandService {

    BrandDTO createBrand(BrandDTO brandDTO);
    BrandDTO getBrandById(Long id);
    Page<BrandDTO> getAllBrands(Pageable pageable);
    BrandDTO updateBrand(Long id,BrandDTO brandDTO);
    void deleteBrand(Long id);
    Long getTotalBrandCount();
    Page<BrandProductCountDTO>  findBrandProductCounts(Pageable pageable);
}
