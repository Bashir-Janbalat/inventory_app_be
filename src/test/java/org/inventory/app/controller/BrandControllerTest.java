package org.inventory.app.controller;

import org.inventory.app.dto.BrandDTO;
import org.inventory.app.model.Brand;
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

@DisplayName("Brand Controller Tests")
public class BrandControllerTest extends BaseControllerTest {

    private List<BrandDTO> testBrands;

    @BeforeEach
    void setUp() {
        brandRepository.deleteAll();
        testBrands = new ArrayList<>();
        testBrands.add(createBrand("Test Brand 1"));
        testBrands.add(createBrand("Test Brand 2"));
        testBrands.add(createBrand("Test Brand 3"));
    }

    @AfterEach
    void tearDown() {
        brandRepository.deleteAll();
        testBrands.clear();
    }


    @Test
    @DisplayName("should create new brand")
    @WithMockUser(roles = {"ADMIN"})
    void createBrand() throws Exception {
        BrandDTO brandDTO = BrandDTO.builder()
                .name("New Test Brand")
                .build();

        String content = MAPPER.writeValueAsString(brandDTO);

        performPostRequest(BASE_URL_BRANDS, content)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(brandDTO.getName()));

        performGetRequest(BASE_URL_BRANDS)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(testBrands.size() + 1));
    }

    @Test
    @DisplayName("should return all brands sorted descending")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnBrandsSortedAscending() throws Exception {
        performGetRequest(BASE_URL_BRANDS + "?page=%d&size=%d&sortDirection=%s", 0, 10, "asc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].name").value(testBrands.get(0).getName()))
                .andExpect(jsonPath("$.content[1].name").value(testBrands.get(1).getName()))
                .andExpect(jsonPath("$.content[2].name").value(testBrands.get(2).getName()));
    }

    @Test
    @DisplayName("should return all brands sorted descending")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnBrandsSortedDescending() throws Exception {
        performGetRequest(BASE_URL_BRANDS + "?page=%d&size=%d&sortDirection=%s", 0, 10, "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.content[0].name").value(testBrands.get(2).getName()))
                .andExpect(jsonPath("$.content[1].name").value(testBrands.get(1).getName()))
                .andExpect(jsonPath("$.content[2].name").value(testBrands.get(0).getName()));
    }

    @Test
    @DisplayName("should return brand by id")
    @WithMockUser(roles = {"ADMIN"})
    void getBrandById() throws Exception {
        Optional<Brand> brand = brandRepository.findByName("Test Brand 1");
        if (brand.isEmpty()) {
            throw new RuntimeException("Brand not found");
        }
        performGetRequest(BASE_URL_BRANDS + "/%d", brand.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Brand 1"));
    }

    @Test
    @DisplayName("should update brand")
    @WithMockUser(roles = {"ADMIN"})
    void updateBrand() throws Exception {
        Optional<Brand> brand = brandRepository.findByName("Test Brand 1");
        if (brand.isEmpty()) {
            throw new RuntimeException("Brand not found");
        }

        performGetRequest(BASE_URL_BRANDS + "/%d", brand.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));

        BrandDTO brandDTO = BrandDTO.builder()
                .name("Updated Brand")
                .build();

        String content = MAPPER.writeValueAsString(brandDTO);

        performPutRequest(BASE_URL_BRANDS + "/%d", content, brand.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(brandDTO.getName()));
    }
}

