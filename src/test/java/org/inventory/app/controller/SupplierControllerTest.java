package org.inventory.app.controller;

import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.model.Supplier;
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

@DisplayName("Supplier Controller Tests")
public class SupplierControllerTest extends BaseControllerTest {

    private List<SupplierDTO> testSuppliers;

    @BeforeEach
    void setUp() {
        supplierRepository.deleteAll();
        testSuppliers = new ArrayList<>();
        testSuppliers.add(supplierService.createSupplier(SupplierDTO.builder().name("Test Supplier 1").
                contactEmail("TestSupplier1@gmail.com").build()));
        testSuppliers.add(supplierService.createSupplier(SupplierDTO.builder().name("Test Supplier 2").
                contactEmail("TestSupplier2@gmail.com").build()));
        testSuppliers.add(supplierService.createSupplier(SupplierDTO.builder().name("Test Supplier 3").
                contactEmail("TestSupplier3@gmail.com").build()));
    }

    @AfterEach
    void tearDown() {
        supplierRepository.deleteAll();
        testSuppliers.clear();
    }


    @Test
    @DisplayName("should create a new supplier")
    @WithMockUser(roles = {"ADMIN"})
    void createSupplier() throws Exception {
        SupplierDTO supplierDTO = SupplierDTO.builder()
                .name("New Test Supplier")
                .contactEmail("Supplier@gmail.com")
                .build();

        String content = MAPPER.writeValueAsString(supplierDTO);

        performPostRequest(BASE_URL_SUPPLIERS, content)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(supplierDTO.getName()))
                .andExpect(jsonPath("$.contactEmail").value(supplierDTO.getContactEmail()));

        performGetRequest(BASE_URL_SUPPLIERS)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(testSuppliers.size()+ 1));
    }

    @Test
    @DisplayName("should return all suppliers sorted ascending")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnSuppliersSortedAscending() throws Exception {
        performGetRequest(BASE_URL_SUPPLIERS + "?page=%d&size=%d&sortDirection=%s", 0, 10, "asc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(testSuppliers.size()))
                .andExpect(jsonPath("$.content[0].name").value(testSuppliers.get(0).getName()))
                .andExpect(jsonPath("$.content[0].contactEmail").value(testSuppliers.get(0).getContactEmail()))
                .andExpect(jsonPath("$.content[1].name").value(testSuppliers.get(1).getName()))
                .andExpect(jsonPath("$.content[1].contactEmail").value(testSuppliers.get(1).getContactEmail()))
                .andExpect(jsonPath("$.content[2].name").value(testSuppliers.get(2).getName()))
                .andExpect(jsonPath("$.content[2].contactEmail").value(testSuppliers.get(2).getContactEmail()));
    }

    @Test
    @DisplayName("should return all suppliers sorted descending")
    @WithMockUser(roles = {"ADMIN"})
    void shouldReturnSuppliersSortedDescending() throws Exception {
        performGetRequest(BASE_URL_SUPPLIERS + "?page=%d&size=%d&sortDirection=%s", 0, 10, "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(testSuppliers.size()))
                .andExpect(jsonPath("$.content[0].name").value(testSuppliers.get(2).getName()))
                .andExpect(jsonPath("$.content[0].contactEmail").value(testSuppliers.get(2).getContactEmail()))
                .andExpect(jsonPath("$.content[1].name").value(testSuppliers.get(1).getName()))
                .andExpect(jsonPath("$.content[1].contactEmail").value(testSuppliers.get(1).getContactEmail()))
                .andExpect(jsonPath("$.content[2].name").value(testSuppliers.get(0).getName()));
    }

    @Test
    @DisplayName("should return supplier by id")
    @WithMockUser(roles = {"ADMIN"})
    void getSupplierById() throws Exception {
        Optional<Supplier> supplier = supplierRepository.findSupplierByName("Test Supplier 1");
        if (supplier.isEmpty()) {
            throw new RuntimeException("Supplier not found");
        }
        performGetRequest(BASE_URL_SUPPLIERS + "/%d", supplier.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Supplier 1"))
                .andExpect(jsonPath("$.contactEmail").value("TestSupplier1@gmail.com"));
    }

    @Test
    @DisplayName("should update supplier")
    @WithMockUser(roles = {"ADMIN"})
    void updateSupplier() throws Exception {
        Optional<Supplier> supplier = supplierRepository.findSupplierByName("Test Supplier 1");
        if (supplier.isEmpty()) {
            throw new RuntimeException("Supplier not found");
        }

        performGetRequest(BASE_URL_SUPPLIERS + "/%d", supplier.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()));

        SupplierDTO supplierDTO = SupplierDTO.builder()
                .name("Updated Supplier")
                .contactEmail("test@gmail.com")
                .build();

        String content = MAPPER.writeValueAsString(supplierDTO);

        performPutRequest(BASE_URL_SUPPLIERS + "/%d", content, supplier.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(supplierDTO.getName()));
    }
}
