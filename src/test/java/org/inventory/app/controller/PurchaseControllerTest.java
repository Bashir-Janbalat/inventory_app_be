package org.inventory.app.controller;

import org.inventory.app.dto.ProductDTO;
import org.inventory.app.dto.PurchaseDTO;
import org.inventory.app.dto.PurchaseItemDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PurchaseControllerTest extends BaseControllerTest {

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should create and get Purchase")
    void createAndGetPurchase() throws Exception {
        ProductDTO productDTO = ProductDTO.builder().name("name")
                .description("description")
                .sku("sku").costPrice(BigDecimal.valueOf(799.99))
                .categoryID(createCategory("category").getId()).brandID(createBrand("brand").getId())
                .supplierID(createSupplier("supplier", "supplier@gmail.com").getId()).build();
        ProductDTO product = productService.createProduct(productDTO);
        WarehouseDTO warehouse = createWarehouse("Lager B", "Lager Str.100");

        PurchaseDTO purchaseDTO = PurchaseDTO.builder().supplierId(createSupplier("Supplier name", "Supplier@gmail.com").getId())
                .items(List.of(PurchaseItemDTO.builder().quantity(100).productId(product.getId())
                        .warehouseId(warehouse.getId()).unitPrice(10).build()))
                .build();
        performPostRequest(BASE_URL_PURCHASES, MAPPER.writeValueAsString(purchaseDTO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplierName").value("Supplier name"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.items[0].quantity").value(100));

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should Fail when the Items list are empty")
    void createPurchaseShouldFailWhenItemsListIsEmpty() throws Exception {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setSupplierId(1L);
        purchaseDTO.setItems(Collections.emptyList());

        performPostRequest(BASE_URL_PURCHASES, MAPPER.writeValueAsString(purchaseDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Validation Failed: {items=Purchase items must not be empty}"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should Fail when the Supplier ID not exists")
    void createPurchaseShouldFailWhenSupplierNotExists() throws Exception {
        PurchaseDTO purchaseDTO = new PurchaseDTO();
        purchaseDTO.setItems(List.of(new PurchaseItemDTO()));

        performPostRequest(BASE_URL_PURCHASES, MAPPER.writeValueAsString(purchaseDTO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.message").value("Validation Failed: {supplierId=SupplierID must not be null}"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return a Purchase by ID")
    void getPurchaseByIdShouldReturnPurchase() throws Exception {
        ProductDTO product = productService.createProduct(ProductDTO.builder()
                .name("Laptop").description("Gaming Laptop").sku("GAM123").costPrice(BigDecimal.valueOf(1000))
                .categoryID(createCategory("Tech").getId())
                .brandID(createBrand("MSI").getId())
                .supplierID(createSupplier("Cool Supplier", "sup@gmail.com").getId())
                .build());
        WarehouseDTO warehouse = createWarehouse("Lager A", "Lager Str.100");

        PurchaseDTO createdPurchase = PurchaseDTO.builder()
                .supplierId(product.getSupplierID())
                .items(List.of(PurchaseItemDTO.builder().productId(product.getId())
                        .quantity(5).unitPrice(999).warehouseId(warehouse.getId()).build()))
                .build();

        String response = performPostRequest(BASE_URL_PURCHASES, MAPPER.writeValueAsString(createdPurchase))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long purchaseId = MAPPER.readTree(response).get("id").asLong();

        performGetRequest(BASE_URL_PURCHASES + "/%d", purchaseId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(purchaseId))
                .andExpect(jsonPath("$.items").isNotEmpty())
                .andExpect(jsonPath("$.supplierName").value("Cool Supplier"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].quantity").value(5))
                .andExpect(jsonPath("$.items[0].unitPrice").value(999));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should update purchase status COMPLETED")
    void updatePurchaseStatusShouldSucceed() throws Exception {
        ProductDTO product = productService.createProduct(ProductDTO.builder()
                .name("Tablet").sku("TAB123").description("iPad").costPrice(BigDecimal.valueOf(500))
                .categoryID(createCategory("Electronics").getId())
                .brandID(createBrand("Apple").getId())
                .supplierID(createSupplier("Apple Supplier", "apple@sup.com").getId())
                .build());

        WarehouseDTO warehouse = createWarehouse("Lager", "Lager Str.100");

        PurchaseDTO purchaseDTO = PurchaseDTO.builder()
                .supplierId(product.getSupplierID())
                .items(List.of(PurchaseItemDTO.builder().productId(product.getId())
                        .quantity(2).unitPrice(499).warehouseId(warehouse.getId()).build()))
                .build();

        String response = performPostRequest(BASE_URL_PURCHASES, MAPPER.writeValueAsString(purchaseDTO))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long purchaseId = MAPPER.readTree(response).get("id").asLong();
        performPutRequestWithParams(BASE_URL_PURCHASES + "/%d/status?status=COMPLETED", purchaseId)
                .andExpect(status().isNoContent());

        performGetRequest(BASE_URL_PURCHASES + "/%d", purchaseId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

}
