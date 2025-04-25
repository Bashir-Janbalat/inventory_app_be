package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.BrandMapper;
import org.inventory.app.model.Brand;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public BrandDTO getBrandById(Long id) {
        return brandMapper.toDto(brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand with ID '" + id + "' not found.")));
    }

    @Override
    public Page<BrandDTO> getAllBrands(Pageable pageable) {
        Page<Brand> brands = brandRepository.findAll(pageable);
        return brands.map(brandMapper::toDto);
    }

    @Override
    public BrandDTO updateBrand(Long id, BrandDTO brandDTO) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with ID '" + id + "' not found."));

        Brand updated = brandMapper.toEntity(brandDTO);
        updated.setId(id);

        Brand saved = brandRepository.save(updated);
        return brandMapper.toDto(saved);
    }

    @Override
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand with ID '" + id + "' not found.");
        }
        brandRepository.deleteById(id);
    }
}
