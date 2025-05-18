package org.inventory.app.mapper;

import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {


    public Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());

        return category;
    }


    public CategoryDTO toDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setCreatedAt(category.getCreatedAt());
        categoryDTO.setUpdatedAt(category.getUpdatedAt());

        return categoryDTO;
    }
}
