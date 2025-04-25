package org.inventory.app.controller;


import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.model.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Category Controller Tests")
public class CategoryControllerTest extends BaseControllerTest {
    private List<CategoryDTO> categoryDTOS;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
        categoryDTOS = new ArrayList<>();
        categoryDTOS.add(categoryService.createCategory(CategoryDTO.builder().name("Test Category 1").build()));
        categoryDTOS.add(categoryService.createCategory(CategoryDTO.builder().name("Test Category 2").build()));
        categoryDTOS.add(categoryService.createCategory(CategoryDTO.builder().name("Test Category 3").build()));
    }

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
        categoryDTOS.clear();
    }


    @Test
    @DisplayName("should create a new category")
    @WithMockUser(roles = {"ADMIN"})
    void createCategory() throws Exception {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("New Test Category")
                .build();

        String content = MAPPER.writeValueAsString(categoryDTO);

        performPostRequest(BASE_URL_CATEGORIES, content)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryDTO.getName()));

        performGetRequest(BASE_URL_CATEGORIES)
                .andExpect(jsonPath("$.totalElements").value(categoryDTOS.size() + 1));
    }

    @Test
    @DisplayName("should return all categories sorted ascending")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnCategoriesSortedAscending() throws Exception {
        performGetRequest(BASE_URL_CATEGORIES + "?page=%d&size=%d&sortDirection=%s", 0, 10, "asc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(categoryDTOS.size()))
                .andExpect(jsonPath("$.content[0].name").value(categoryDTOS.get(0).getName()))
                .andExpect(jsonPath("$.content[1].name").value(categoryDTOS.get(1).getName()))
                .andExpect(jsonPath("$.content[2].name").value(categoryDTOS.get(2).getName()));
    }

    @Test
    @DisplayName("should return all categories sorted descending")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnCategoriesSortedDescending() throws Exception {
        performGetRequest(BASE_URL_CATEGORIES + "?page=%d&size=%d&sortDirection=%s", 0, 10, "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(categoryDTOS.size()))
                .andExpect(jsonPath("$.content[0].name").value(categoryDTOS.get(2).getName()))
                .andExpect(jsonPath("$.content[1].name").value(categoryDTOS.get(1).getName()))
                .andExpect(jsonPath("$.content[2].name").value(categoryDTOS.get(0).getName()));
    }

    @Test
    @DisplayName("should return category by id")
    @WithMockUser(roles = {"ADMIN"})
    void getCategoryById() throws Exception {
        Optional<Category> category = categoryRepository.findCategoryByName("Test Category 1");
        if (category.isEmpty()) {
            throw new RuntimeException("Category not found");
        }
        performGetRequest(BASE_URL_CATEGORIES + "/%d", category.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Category 1"));
    }

    @Test
    @DisplayName("should update category")
    @WithMockUser(roles = {"ADMIN"})
    void updateCategory() throws Exception {
        Optional<Category> category = categoryRepository.findCategoryByName("Test Category 1");
        if (category.isEmpty()) {
            throw new RuntimeException("Category not found");
        }

        performGetRequest(BASE_URL_CATEGORIES + "/%d", category.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));

        CategoryDTO categoryDTO = CategoryDTO.builder()
                .name("Updated Category")
                .build();

        String content = MAPPER.writeValueAsString(categoryDTO);

        performPutRequest(BASE_URL_CATEGORIES + "/%d", content, category.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(categoryDTO.getName()));
    }
}
