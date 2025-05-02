package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.exception.DuplicateResourceException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.BrandMapper;
import org.inventory.app.model.Brand;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    @Transactional
    public BrandDTO createBrand(BrandDTO brandDTO) {
        String name = brandDTO.getName().trim();
        brandRepository.findByName(name).ifPresent(value -> {
            throw new AlreadyExistsException("Brand", "name", name);
        });
        Brand savedBrand = brandRepository.save(brandMapper.toEntity(brandDTO));
        return brandMapper.toDto(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDTO getBrandById(Long id) {
        return brandMapper.toDto(brandRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Brand with ID '" + id + "' not found.")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BrandDTO> getAllBrands(Pageable pageable) {
        Page<Brand> brands = brandRepository.findAll(pageable);
        return brands.map(brandMapper::toDto);
    }

    @Override
    @Transactional
    public BrandDTO updateBrand(Long id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand with ID '" + id + "' not found."));
        String name = brandDTO.getName().trim();
        brandRepository.findByName(name).ifPresent(existingBrand -> {
            if (!existingBrand.getId().equals(id)) {
                throw new DuplicateResourceException("Brand name '" + brandDTO.getName() + "' already exists.");
            }
        });

        brand.setName(brandDTO.getName());

        Brand saved = brandRepository.save(brand);
        return brandMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand with ID '" + id + "' not found.");
        }
        brandRepository.deleteById(id);
    }
}
