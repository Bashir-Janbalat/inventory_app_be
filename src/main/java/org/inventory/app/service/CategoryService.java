package org.inventory.app.service;

import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.projection.CategoryStatsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO getCategoryById(Long id);
    Page<CategoryDTO> getAllCategories(Pageable pageable);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    Long getTotalCategoryCount();

    Page<CategoryStatsDTO> findCategoriesWithStats(Pageable pageable);
}
