package org.inventory.app.mapper;

import org.inventory.app.dto.BrandDTO;
import org.inventory.app.model.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {


    public Brand toEntity(BrandDTO brandDTO) {
        if (brandDTO == null) {
            return null;
        }
        Brand brand = new Brand();
        brand.setId(brandDTO.getId());
        brand.setName(brandDTO.getName());

        return brand;
    }

    public BrandDTO toDto(Brand brand) {
        if (brand == null) {
            return null;
        }
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(brand.getId());
        brandDTO.setName(brand.getName());

        return brandDTO;
    }
}
