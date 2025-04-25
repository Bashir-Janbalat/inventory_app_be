package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.CategoryMapper;
import org.inventory.app.model.Category;
import org.inventory.app.repository.CategoryRepository;
import org.inventory.app.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category with ID '" + id + "' not found.")));

    }

    @Override
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toDto);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID '" + id + "' not found."));

        Category updated = categoryMapper.toEntity(categoryDTO);
        updated.setId(id);

        Category saved = categoryRepository.save(updated);
        return categoryMapper.toDto(saved);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category with ID '" + id + "' not found.");
        }
        categoryRepository.deleteById(id);
    }
}
