package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.exception.DuplicateResourceException;
import org.inventory.app.exception.EntityHasAssociatedItemsException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.BrandMapper;
import org.inventory.app.model.Brand;
import org.inventory.app.repository.BrandRepository;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.service.BrandService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional

    @Caching(evict = {
            @CacheEvict(value = {"brands", "brand", "brandCount"}, allEntries = true),
            @CacheEvict(value = {"brandStats"}, allEntries = true), // on Dashboard
            @CacheEvict(value = {"dashboardSummary"}, allEntries = true)
    })
    public BrandDTO createBrand(BrandDTO brandDTO) {
        String name = brandDTO.getName().trim();
        brandRepository.findByName(name).ifPresent(value -> {
            log.warn("Attempt to create duplicate brand with name '{}'", name);
            throw new AlreadyExistsException("Brand", "name", name);
        });

        Brand savedBrand = brandRepository.save(brandMapper.toEntity(brandDTO));
        log.info("Created new brand with ID: {}. Cache 'brands', 'brand', 'brandCount', 'brandStats','dashboardSummary' evicted.", savedBrand.getId());
        return brandMapper.toDto(savedBrand);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "brand", key = "#id")
    public BrandDTO getBrandById(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Brand with ID {} not found.", id);
                    return new ResourceNotFoundException("Brand with ID '" + id + "' not found.");
                });

        log.info("Fetched brand with ID: {} from DB (and cached in 'brand')", id);
        return brandMapper.toDto(brand);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "brands", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<BrandDTO> getAllBrands(Pageable pageable) {
        Page<Brand> brands = brandRepository.findAll(pageable);
        log.info("Fetched {} brands from DB (page {} size {}) (and cached in brands)", brands.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(brands.map(brandMapper::toDto));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"brands", "brand", "brandCount"}, allEntries = true),
            @CacheEvict(value = {"brandStats"}, allEntries = true) // on Dashboard
    })
    public BrandDTO updateBrand(Long id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Brand with ID {} not found for update.", id);
                    return new ResourceNotFoundException("Brand with ID '" + id + "' not found.");
                });

        String name = brandDTO.getName().trim();
        brandRepository.findByName(name).ifPresent(existingBrand -> {
            if (!existingBrand.getId().equals(id)) {
                log.warn("Duplicate brand name '{}' found for different ID {}", name, existingBrand.getId());
                throw new DuplicateResourceException("Brand name '" + name + "' already exists.");
            }
        });

        brand.setName(name);
        Brand saved = brandRepository.save(brand);
        log.info("Updated brand with ID: {}. Cache 'brands', 'brand', 'brandCount', 'brandStats' evicted.", id);
        return brandMapper.toDto(saved);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = {"brands", "brand", "brandCount"}, allEntries = true),
            @CacheEvict(value = {"brandStats"}, allEntries = true), // on Dashboard
            @CacheEvict(value = {"dashboardSummary"}, allEntries = true)
    })
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent brand with ID {}", id);
            throw new ResourceNotFoundException("Brand with ID '" + id + "' not found.");
        }
        if (productRepository.existsByBrandId(id)) {
            log.warn("Attempted to delete Brand with ID {} that has associated products.", id);
            throw new EntityHasAssociatedItemsException("Brand", id);
        }

        brandRepository.deleteById(id);
        log.info("Deleted brand with ID: {}. Cache 'brands', 'brand', 'brandCount', 'brandStats', 'dashboardSummary' evicted.", id);
    }

    @Override
    @Cacheable(value = "brandCount")
    public ValueWrapper<Long> getTotalBrandCount() {
        Long count = brandRepository.count();
        log.info("Fetched Brand size from DB (and cached in 'brandCount'): {}", count);
        return new ValueWrapper<>(count);
    }

}
