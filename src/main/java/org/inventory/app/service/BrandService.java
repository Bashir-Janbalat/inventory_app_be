package org.inventory.app.service;

import org.inventory.app.dto.BrandDTO;

import java.util.List;

public interface BrandService {

    BrandDTO createBrand(BrandDTO brandDTO);
    BrandDTO getBrandById(Long id);
    List<BrandDTO> getAllBrands();
    BrandDTO updateBrand(Long id,BrandDTO brandDTO);
    void deleteBrand(Long id);
}
