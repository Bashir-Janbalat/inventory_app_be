package org.inventory.app.service;

import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.dto.PagedResponseDTO;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO getCategoryById(Long id);
    PagedResponseDTO<CategoryDTO> getAllCategories(Pageable pageable);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    ValueWrapper<Long> getTotalCategoryCount();
}
