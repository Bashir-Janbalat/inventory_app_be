package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.exception.DuplicateResourceException;
import org.inventory.app.exception.EntityHasAssociatedItemsException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.CategoryMapper;
import org.inventory.app.model.Category;
import org.inventory.app.projection.CategoryStatsDTO;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.repository.ProductRepository;
import org.inventory.app.service.CategoryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "category", "categoryCount", "categoryStats"}, allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        String name = categoryDTO.getName().trim();
        categoryRepository.findByName(name).ifPresent(value -> {
            log.warn("Attempt to create duplicate category with name '{}'", name);
            throw new AlreadyExistsException("Category", "name", name);
        });

        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        log.info("Created new category with ID {} Cache 'categories','category','categoryCount' evicted", savedCategory.getId());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "category", key = "#id")
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Category with ID {} not found.", id);
            return new ResourceNotFoundException("Category with ID '" + id + "' not found.");
        });

        log.info("Fetched category with ID {} from DB (and cached in 'category')", id);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        log.info("Fetched {} categories (page {} size {}) from DB (and cached in 'categories')", categories.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(categories.map(categoryMapper::toDto));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "category", "categoryCount", "categoryStats"}, allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existing = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Category with ID {} not found for update.", id);
            return new ResourceNotFoundException("Category with ID '" + id + "' not found.");
        });

        String name = categoryDTO.getName().trim();
        categoryRepository.findByName(name).ifPresent(otherCategory -> {
            if (!otherCategory.getId().equals(id)) {
                log.warn("Duplicate category name '{}' found for different ID {}", name, otherCategory.getId());
                throw new DuplicateResourceException("Category name '" + name + "' already exists.");
            }
        });

        existing.setName(name);
        Category saved = categoryRepository.save(existing);
        log.info("Updated category with ID {} Cache 'categories','category','categoryCount' evicted", id);
        return categoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "category", "categoryCount", "categoryStats"}, allEntries = true)
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            log.warn("Attempted to delete non-existent category with ID {}", id);
            throw new ResourceNotFoundException("Category with ID '" + id + "' not found.");
        }
        if (productRepository.existsByCategoryId(id)) {
            log.warn("Attempted to delete category with ID {} that has associated products.", id);
            throw new EntityHasAssociatedItemsException("Category", id);
        }
        categoryRepository.deleteById(id);
        log.info("Deleted category with ID {} Cache 'categories','category','categoryCount' evicted", id);
    }

    @Override
    @Cacheable(value = "categoryCount")
    public ValueWrapper<Long> getTotalCategoryCount() {
        Long count = categoryRepository.count();
        log.info("Fetched category size from DB (and cached in 'categoryCount'): {}", count);
        return new ValueWrapper<>(count);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categoryStats", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<CategoryStatsDTO> findCategoriesWithStats(Pageable pageable) {
        Page<CategoryStatsDTO> categories = categoryRepository.findCategoryStats(pageable);
        log.info("Fetched {} categories with stats (page {} size {}) from DB (and cached in 'categoryStats')", categories.getTotalElements(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(categories);
    }
}
