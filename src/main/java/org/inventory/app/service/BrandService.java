package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.springframework.data.domain.Pageable;

public interface BrandService {

    BrandDTO createBrand(BrandDTO brandDTO);

    BrandDTO getBrandById(Long id);

    PagedResponseDTO<BrandDTO> getAllBrands(Pageable pageable);

    BrandDTO updateBrand(Long id, BrandDTO brandDTO);

    void deleteBrand(Long id);

    ValueWrapper<Long> getTotalBrandCount();


}
