package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.mapper.BrandMapper;
import org.inventory.app.model.Brand;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand savedBrand = brandRepository.save(brandMapper.toEntity(brandDTO));
        return brandMapper.toDto(savedBrand);
    }

    @Override
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAll().stream().map(brandMapper::toDto).collect(Collectors.toList());
    }
}
