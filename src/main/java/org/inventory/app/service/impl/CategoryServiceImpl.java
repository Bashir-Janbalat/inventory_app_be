package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.exception.DuplicateResourceException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.CategoryMapper;
import org.inventory.app.model.Category;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        String name = categoryDTO.getName().trim();
        categoryRepository.findByName(name).ifPresent(value -> {
            throw new AlreadyExistsException("Category", "name", name);
        });
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category with ID '" + id + "' not found.")));

    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toDto);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID '" + id + "' not found."));
        String name = categoryDTO.getName().trim();
        categoryRepository.findByName(name).ifPresent(existingCategory -> {
            if (!existingCategory.getId().equals(id)) {
                throw new DuplicateResourceException("Category name '" + categoryDTO.getName() + "' already exists.");
            }
        });
        Category updated = categoryMapper.toEntity(categoryDTO);
        updated.setId(id);

        Category saved = categoryRepository.save(updated);
        return categoryMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with ID '" + id + "' not found.");
        }
        categoryRepository.deleteById(id);
    }
}
