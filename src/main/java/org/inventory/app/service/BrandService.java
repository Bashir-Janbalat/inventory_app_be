package org.inventory.app.service;

import org.inventory.app.dto.BrandDTO;

import java.util.List;

public interface BrandService {

    BrandDTO createBrand(BrandDTO brandDTO);
    List<BrandDTO> getAllBrands();
}
