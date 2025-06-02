package org.inventory.app.controller;

import org.inventory.app.dto.*;
import org.inventory.app.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Product Controller Tests")
public class ProductControllerTest extends BaseControllerTest {


    private static final String SAMSUNG_SKU = "SM-S918BUGGED";
    private static final String BOSCH_SKU = "WAU28S80AT";


    @BeforeEach
    public void setup() {
        // Setup shared data
        CategoryDTO smartphoneCategory = createCategory("Smartphones");
        BrandDTO samsungBrand = createBrand("Samsung");
        SupplierDTO samsungSupplier = createSupplier("Samsung Electronics GmbH", "b2b.support@samsung.de");
        WarehouseDTO firstWarehouse = createWarehouse("Warehouse", "Muenchen-Zentral");
        WarehouseDTO secondWarehouse = createWarehouse("Warehouse", "Berlin-Zentral");

        // Samsung Phone Setup
        ProductDTO samsungProductDTO = ProductDTO.builder().name("Samsung Galaxy S23 Ultra")
                .description("6.8 Dynamic AMOLED 2X Display, 200MP Hauptkamera, 5000mAh Akku, 12GB RAM, 512GB Speicher")
                .sku(SAMSUNG_SKU).costPrice(BigDecimal.valueOf(1399.99))
                .categoryID(smartphoneCategory.getId()).brandID(samsungBrand.getId()).supplierID(samsungSupplier.getId())
                .images(List.of(new ImageDTO(
                        "https://assets.samsung.com/de/smartphones/galaxy-s23-ultra/images/galaxy-s23-ultra-green.png",
                        "Samsung Galaxy S23 Ultra in Botanic Green"
                ))).stocks(List.of(new StockDTO(50, firstWarehouse), new StockDTO(100, secondWarehouse)))
                .productAttributes(List.of(
                        new ProductAttributeDTO("color", "Botanic Green"),
                        new ProductAttributeDTO("storage", "512GB")
                )).build();
        productService.createProduct(samsungProductDTO);


        // Bosch Washer Setup
        ProductDTO boschWasherProductDTO = ProductDTO.builder().name("BOSCH Serie 6 WAU28S80")
                .description("Waschmaschine, 9 kg, 1400 U/min., EcoSilence Drive, SpeedPerfect, AllergiePlus, Nachlegefunktion")
                .sku(BOSCH_SKU).costPrice(BigDecimal.valueOf(799.99))
                .categoryID(createCategory("Haushaltsgeraete").getId()).brandID(createBrand("BOSCH").getId())
                .supplierID(createSupplier("ElectronicPartner Deutschland", "grosshandel@ep-deutschland.de").getId())
                .images(List.of(new ImageDTO(
                        "https://media3.bosch-home.com/Product_Shots/1600x900/WAU28S80AT_def.png",
                        "BOSCH Serie 6 Waschmaschine Frontansicht"
                ))).stocks(List.of(new StockDTO(25, firstWarehouse)))
                .productAttributes(List.of(
                        new ProductAttributeDTO("energieeffizienzklasse", "A+++"),
                        new ProductAttributeDTO("fassungsvermögen", "9 KG")
                )).build();
        productService.createProduct(boschWasherProductDTO);
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should handle invalid Product Data")
    void shouldHandleInvalidProductData() throws Exception {
        String message = "Validation Failed: {brandID=Brand-ID is required, name=Product name is required, sku=SKU is required, categoryID=Category-ID is required}";
        performPostRequest(BASE_URL_PRODUCTS, "{}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message));

    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should create a new product")
    public void createNewLaptopProductShouldReturnCreatedProduct() throws Exception {
        CategoryDTO laptops = createCategory("Laptops");
        BrandDTO apple = createBrand("Apple");
        SupplierDTO appleDE = createSupplier("Apple Deutschland GmbH", "apple.support@apple.de");

        ProductDTO productDto = ProductDTO.builder()
                .name("Apple MacBook Pro 16")
                .sku("MBP16-M2-32GB")
                .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                .costPrice(BigDecimal.valueOf(3499.99))
                .categoryID(laptops.getId())
                .brandID(apple.getId())
                .supplierID(appleDE.getId())
                .images(List.of(ImageDTO.builder()
                        .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                        .altText("MacBook Pro 16 Zoll in Space Grau")
                        .build()))
                .stocks(List.of(StockDTO.builder()
                        .quantity(15)
                        .warehouse(createWarehouse("Muenchen-Zentral", "Muenchen-Zentral-address"))
                        .build()))
                .productAttributes(List.of(
                        ProductAttributeDTO.builder()
                                .attributeName("processor")
                                .attributeValue("Apple M2 Max")
                                .build(),
                        ProductAttributeDTO.builder()
                                .attributeName("ram")
                                .attributeValue("32GB")
                                .build(),
                        ProductAttributeDTO.builder()
                                .attributeName("storage")
                                .attributeValue("1TB SSD")
                                .build()
                ))
                .build();
        String productJson = MAPPER.writeValueAsString(productDto);


        performPostRequest(BASE_URL_PRODUCTS, productJson)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Apple MacBook Pro 16")))
                .andExpect(jsonPath("$.sku", is("MBP16-M2-32GB")))
                .andExpect(jsonPath("$.description", is("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")))
                .andExpect(jsonPath("$.costPrice", is(3499.99)))
                .andExpect(jsonPath("$.categoryName", is("Laptops")))
                .andExpect(jsonPath("$.brandName", is("Apple")))
                .andExpect(jsonPath("$.supplierName", is("Apple Deutschland GmbH")))
                .andExpect(jsonPath("$.supplierContactEmail", is("apple.support@apple.de")))
                .andExpect(jsonPath("$.stocks[0].quantity", is(15)))
                .andExpect(jsonPath("$.stocks[0].warehouse.address", is("Muenchen-Zentral-address")))
                .andExpect(jsonPath("$.images[0].imageUrl", is("https://store.apple.com/macbook-pro-16-space-gray.png")))
                .andExpect(jsonPath("$.images[0].altText", is("MacBook Pro 16 Zoll in Space Grau")))
                .andExpect(jsonPath("$.productAttributes", hasSize(3)))
                .andExpect(jsonPath("$.productAttributes[0].attributeName", is("processor")))
                .andExpect(jsonPath("$.productAttributes[0].attributeValue", is("Apple M2 Max")))
                .andExpect(jsonPath("$.productAttributes[1].attributeName", is("ram")))
                .andExpect(jsonPath("$.productAttributes[1].attributeValue", is("32GB")))
                .andExpect(jsonPath("$.productAttributes[2].attributeName", is("storage")))
                .andExpect(jsonPath("$.productAttributes[2].attributeValue", is("1TB SSD")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should filter products by search term, categoryName and brandName")
    public void shouldFilterMacBookOnly() throws Exception {
        // 1) Setup: Kategorie, Marken und Supplier anlegen
        CategoryDTO laptops = createCategory("Laptops");
        BrandDTO apple = createBrand("Apple");
        SupplierDTO appleDE = createSupplier("Apple Deutschland GmbH", "apple.support@apple.de");

        // 2) Erstes Produkt: Apple MacBook Pro 16
        ProductDTO macBook = ProductDTO.builder()
                .name("Apple MacBook Pro 16")
                .sku("MBP16-M2-32GB")
                .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                .costPrice(BigDecimal.valueOf(3499.99))
                .categoryID(laptops.getId())
                .brandID(apple.getId())
                .supplierID(appleDE.getId())
                .images(List.of(
                        ImageDTO.builder()
                                .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                                .altText("MacBook Pro 16 Zoll in Space Grau")
                                .build()
                ))
                .stocks(List.of(StockDTO.builder()
                        .quantity(15)
                        .warehouse(createWarehouse("Muenchen-Zentral", "Muenchen-Zentral-address"))
                        .build())
                )
                .productAttributes(List.of(
                        ProductAttributeDTO.builder().attributeName("processor").attributeValue("Apple M2 Max").build(),
                        ProductAttributeDTO.builder().attributeName("ram").attributeValue("32GB").build(),
                        ProductAttributeDTO.builder().attributeName("storage").attributeValue("1TB SSD").build()
                ))
                .build();

        performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(macBook))
                .andExpect(status().isCreated());

        // 3) Zweites Produkt: Dell XPS 13
        BrandDTO dell = createBrand("Dell");
        SupplierDTO dellDE = createSupplier("Dell Germany GmbH", "support@dell.de");

        ProductDTO xps13 = ProductDTO.builder()
                .name("Dell XPS 13")
                .sku("XPS13-9300")
                .description("13 Zoll InfinityEdge Display, Intel i7, 16GB RAM, 512GB SSD")
                .costPrice(BigDecimal.valueOf(1299.00))
                .categoryID(laptops.getId())
                .brandID(dell.getId())
                .supplierID(dellDE.getId())
                .images(List.of(
                        ImageDTO.builder()
                                .imageUrl("https://example.com/dell-xps-13.png")
                                .altText("Dell XPS 13")
                                .build()
                ))
                .stocks(List.of(StockDTO.builder()
                        .quantity(10)
                        .warehouse(createWarehouse("Muenchen-Zentral", "Muenchen-Zentral-address"))
                        .build())
                )
                .productAttributes(List.of(
                        ProductAttributeDTO.builder().attributeName("processor").attributeValue("Intel i7").build(),
                        ProductAttributeDTO.builder().attributeName("ram").attributeValue("16GB").build(),
                        ProductAttributeDTO.builder().attributeName("storage").attributeValue("512GB SSD").build()
                ))
                .build();

        performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(xps13))
                .andExpect(status().isCreated());

        // 4) GET mit allen drei Filter-Parametern (MacBook, Laptops, Apple)
        performGetRequest(
                BASE_URL_PRODUCTS +
                "?page=%d&size=%d&sortBy=%s&sortDirection=%s&searchBy=%s&categoryName=%s&brandName=%s",
                0, 10, "name", "asc",
                "MacBook", laptops.getName(), apple.getName()
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Apple MacBook Pro 16"))
                .andExpect(jsonPath("$.content[0].categoryName").value("Laptops"))
                .andExpect(jsonPath("$.content[0].brandName").value("Apple"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should filter products by categoryName only")
    public void shouldFilterProductsByCategoryName() throws Exception {
        // 1) Setup: zwei Kategorien, eine für Laptops und eine für Smartphones
        CategoryDTO laptops = createCategory("Laptops");
        CategoryDTO phones = createCategory("phones");

        // Brands und Supplier (wiederverwendbar)
        BrandDTO otto = createBrand("Otto");
        SupplierDTO appleDE = createSupplier("Otto Deutschland GmbH", "Otto.support@apple.de");

        BrandDTO dell = createBrand("Dell");
        SupplierDTO dellDE = createSupplier("Dell Germany GmbH", "support@dell.de");

        // 2) Produkt in Kategorie "Laptops"
        ProductDTO macBook = ProductDTO.builder()
                .name("Apple MacBook Pro 16")
                .sku("MBP16-M2-32GB")
                .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                .costPrice(BigDecimal.valueOf(3499.99))
                .categoryID(laptops.getId())
                .brandID(otto.getId())
                .supplierID(appleDE.getId())
                .images(List.of(
                        ImageDTO.builder()
                                .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                                .altText("MacBook Pro 16 Zoll in Space Grau")
                                .build()
                ))
                .stocks(List.of(StockDTO.builder()
                        .quantity(15)
                        .warehouse(createWarehouse("Muenchen-Zentral", "Muenchen-Zentral-address"))
                        .build())
                )
                .productAttributes(List.of(
                        ProductAttributeDTO.builder().attributeName("processor").attributeValue("Apple M2 Max").build(),
                        ProductAttributeDTO.builder().attributeName("ram").attributeValue("32GB").build(),
                        ProductAttributeDTO.builder().attributeName("storage").attributeValue("1TB SSD").build()
                ))
                .build();
        performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(macBook))
                .andExpect(status().isCreated());

        // 3) Produkt in Kategorie "Smartphones"
        ProductDTO galaxy = ProductDTO.builder()
                .name("Samsung Galaxy S21")
                .sku("SGS21-5G")
                .description("6.2 Zoll Dynamic AMOLED, Exynos 2100, 8GB RAM, 128GB Speicher")
                .costPrice(BigDecimal.valueOf(849.00))
                .categoryID(phones.getId())
                .brandID(createBrand("Samsung-Brand").getId())
                .supplierID(createSupplier("Samsung Germany GmbH", "support@samsung.de")
                        .getId())
                .images(List.of(
                        ImageDTO.builder()
                                .imageUrl("https://example.com/galaxy-s21.png")
                                .altText("Samsung Galaxy S21")
                                .build()
                ))
                .stocks(List.of(StockDTO.builder()
                        .quantity(20)
                        .warehouse(createWarehouse("Muenchen-Zentral", "Muenchen-Zentral-address"))
                        .build())
                )
                .productAttributes(List.of(
                        ProductAttributeDTO.builder().attributeName("display").attributeValue("Dynamic AMOLED").build(),
                        ProductAttributeDTO.builder().attributeName("battery").attributeValue("4000mAh").build()
                ))
                .build();
        performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(galaxy))
                .andExpect(status().isCreated());

        // 4) GET-Request nur mit categoryName=Laptops
        performGetRequest(
                BASE_URL_PRODUCTS + "?page=%d&size=%d&sortBy=%s&sortDirection=%s&categoryName=%s",
                0, 10, "name", "asc", laptops.getName()
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Apple MacBook Pro 16"))
                .andExpect(jsonPath("$.content[0].categoryName").value("Laptops"));
    }


    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return paginated products")
    public void shouldReturnPaginatedProducts() throws Exception {
        ResultActions result = performGetRequest(BASE_URL_PRODUCTS + "?page=%d&size=%d&sortDirection=%s", 0, 1, "asc")
                .andExpect(status().isOk());
        assertProductBoschWasher(result, "$.content[0]");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return products sorted ascending")
    public void shouldReturnProductsSortedAscending() throws Exception {
        ResultActions result = performGetRequest(BASE_URL_PRODUCTS + "?page=%d&size=%d&sortDirection=%s", 0, 10, "asc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));

        assertProductBoschWasher(result, "$.content[0]");
        assertProductSamsungPhone(result, "$.content[1]");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return products sorted descending")
    public void shouldReturnProductsSortedDescending() throws Exception {

        ResultActions result = performGetRequest(BASE_URL_PRODUCTS + "?page=%d&size=%d&sortDirection=%s", 0, 10, "desc")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
        assertProductSamsungPhone(result, "$.content[0]");
        assertProductBoschWasher(result, "$.content[1]");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return product by Id")
    public void shouldReturnProductByIdSamsungPhone() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        ResultActions result = performGetRequest(BASE_URL_PRODUCTS + "/%d", product.get().getId())
                .andExpect(status().isOk());

        assertProductSamsungPhone(result, "$");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return product by Id")
    public void shouldReturnProductByIdBoschWasher() throws Exception {
        Optional<Product> product = productRepository.findBySku(BOSCH_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        ResultActions result = performGetRequest(BASE_URL_PRODUCTS + "/%d", product.get().getId())
                .andExpect(status().isOk());

        assertProductBoschWasher(result, "$");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return 404 when product not found")
    void shouldReturn404WhenProductNotFound() throws Exception {
        performGetRequest(BASE_URL_PRODUCTS + "/%d", 999)
                .andExpect(status().isNotFound());
    }

    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return products within time limit")
    void shouldReturnProductsWithinTimeLimit() throws Exception {
        performGetRequest(BASE_URL_PRODUCTS + "?page=%d&size=%d", 0, 100)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return product searched by name")
    void shouldReturnProductSearchedBy() throws Exception {

        CategoryDTO laptops = createCategory("Laptops");
        BrandDTO apple = createBrand("Apple");
        SupplierDTO appleDE = createSupplier("Apple Deutschland GmbH", "apple.support@apple.de");

        ProductDTO productDto = ProductDTO.builder()
                .name("SearchBy Product")
                .sku("MBP16-M2-32GB")
                .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                .costPrice(BigDecimal.valueOf(3499.99))
                .categoryID(laptops.getId())
                .brandID(apple.getId())
                .supplierID(appleDE.getId())
                .images(List.of(ImageDTO.builder()
                        .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                        .altText("MacBook Pro 16 Zoll in Space Grau")
                        .build()))
                .stocks(List.of(StockDTO.builder()
                        .quantity(15)
                        .warehouse(createWarehouse("Muenchen-Zentral", "Muenchen-Zentral-address"))
                        .build()))
                .productAttributes(List.of(
                        ProductAttributeDTO.builder()
                                .attributeName("processor")
                                .attributeValue("Apple M2 Max")
                                .build(),
                        ProductAttributeDTO.builder()
                                .attributeName("ram")
                                .attributeValue("32GB")
                                .build(),
                        ProductAttributeDTO.builder()
                                .attributeName("storage")
                                .attributeValue("1TB SSD")
                                .build()
                ))
                .build();

        String productJson = MAPPER.writeValueAsString(productDto);

        performPostRequest(BASE_URL_PRODUCTS, productJson)
                .andExpect(status().isCreated());

        performGetRequest(
                String.format(BASE_URL_PRODUCTS + "?page=0&size=100&searchBy=%s", "SearchBy Product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].name").value("SearchBy Product"))
                .andExpect(jsonPath("$.content[0].sku").value("MBP16-M2-32GB"))
                .andExpect(jsonPath("$.content[0].costPrice").value(3499.99))
                .andExpect(jsonPath("$.content[0].description").value("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should update existing product")
    void shouldUpdateExistingProduct() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        ProductDTO updateDto = ProductDTO.builder()
                .name("Samsung Galaxy S23 Ultra Updated")
                .sku(SAMSUNG_SKU)
                .description("Updated description")
                .costPrice(BigDecimal.valueOf(1299.99))
                .categoryID(product.get().getCategory().getId())
                .brandID(product.get().getBrand().getId())
                .supplierID(product.get().getSupplier().getId())
                .build();
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), product.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Samsung Galaxy S23 Ultra Updated")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.costPrice", is(1299.99)));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should update existing product with an other Category")
    void shouldUpdateExistingProductOtherCategory() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        CategoryDTO testCategory = createCategory("OtherCategory");
        ProductDTO updateDto = ProductDTO.builder()
                .name("Samsung Galaxy S23 Ultra Updated")
                .sku(SAMSUNG_SKU)
                .description("Updated description")
                .costPrice(BigDecimal.valueOf(1299.99))
                .categoryID(testCategory.getId())
                .brandID(product.get().getBrand().getId())
                .supplierID(product.get().getSupplier().getId())
                .build();
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), product.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Samsung Galaxy S23 Ultra Updated")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.costPrice", is(1299.99)))
                .andExpect(jsonPath("$.categoryName", is("OtherCategory")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should update existing product with an other Supplier")
    void shouldUpdateExistingProductOtherSupplier() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        CategoryDTO testCategory = createCategory("OtherCategory");
        SupplierDTO testSupplier = createSupplier("TestSupplier", "test@gmail.com");
        ProductDTO updateDto = ProductDTO.builder()
                .name("Samsung Galaxy S23 Ultra Updated")
                .sku(SAMSUNG_SKU)
                .description("Updated description")
                .costPrice(BigDecimal.valueOf(1299.99))
                .categoryID(testCategory.getId())
                .brandID(product.get().getBrand().getId())
                .supplierID(testSupplier.getId())
                .build();
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), product.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Samsung Galaxy S23 Ultra Updated")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.costPrice", is(1299.99)))
                .andExpect(jsonPath("$.categoryName", is("OtherCategory")))
                .andExpect(jsonPath("$.supplierName", is("TestSupplier")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should update existing product with a new Image")
    void shouldUpdateExistingProductNewImage() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        ImageDTO dto = new ImageDTO("https://example.com/test-image-updated.png", "Test Image Updated");
        ProductDTO updateDto = ProductDTO.builder()
                .name("Samsung Galaxy S23 Ultra Updated")
                .sku(SAMSUNG_SKU)
                .description("Updated description")
                .costPrice(BigDecimal.valueOf(1299.99))
                .categoryID(product.get().getCategory().getId())
                .brandID(product.get().getBrand().getId())
                .supplierID(product.get().getSupplier().getId())
                .images(List.of(dto))
                .build();
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), product.get().getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Samsung Galaxy S23 Ultra Updated")))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.costPrice", is(1299.99)))
                .andExpect(jsonPath("$.images", hasSize(1)))
                .andExpect(jsonPath("$.images[0].imageUrl", is("https://example.com/test-image-updated.png")))
                .andExpect(jsonPath("$.images[0].altText", is("Test Image Updated")));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should return 404 when updating non existent product")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        ProductDTO updateDto = ProductDTO.builder()
                .name("Samsung Galaxy S23 Ultra Updated")
                .sku(SAMSUNG_SKU)
                .description("Updated description")
                .costPrice(BigDecimal.valueOf(1299.99))
                .categoryID(product.get().getCategory().getId())
                .brandID(product.get().getBrand().getId())
                .supplierID(product.get().getSupplier().getId())
                .build();
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), 999)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return unauthorized for update without token")
    void shouldReturnUnauthorizedForUpdateWithoutToken() throws Exception {
        ProductDTO updateDto = new ProductDTO();
        updateDto.setName("Test Product");
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), 1)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("should handle invalid product data for update")
    void shouldHandleInvalidProductDataForUpdate() throws Exception {
        Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        ProductDTO invalidDto = new ProductDTO();
        performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(invalidDto), product.get().getId())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


    private void assertProductBoschWasher(ResultActions actions, String prefix) throws Exception {
        prefix = (prefix == null) ? "$" : prefix;
        actions.andExpect(jsonPath(prefix + ".name", is("BOSCH Serie 6 WAU28S80")))
                .andExpect(jsonPath(prefix + ".sku", is(BOSCH_SKU)))
                .andExpect(jsonPath(prefix + ".description", is("Waschmaschine, 9 kg, 1400 U/min., EcoSilence Drive, SpeedPerfect, AllergiePlus, Nachlegefunktion")))
                .andExpect(jsonPath(prefix + ".costPrice", is(799.99)))
                .andExpect(jsonPath(prefix + ".categoryName", is("Haushaltsgeraete")))
                .andExpect(jsonPath(prefix + ".brandName", is("BOSCH")))
                .andExpect(jsonPath(prefix + ".supplierName", is("ElectronicPartner Deutschland")))
                .andExpect(jsonPath(prefix + ".supplierContactEmail", is("grosshandel@ep-deutschland.de")))
                .andExpect(jsonPath(prefix + ".stocks[0].quantity", is(25)))
                .andExpect(jsonPath(prefix + ".stocks[0].warehouse.address", is("Muenchen-Zentral")))
                .andExpect(jsonPath(prefix + ".images[0].imageUrl", is("https://media3.bosch-home.com/Product_Shots/1600x900/WAU28S80AT_def.png")))
                .andExpect(jsonPath(prefix + ".images[0].altText", is("BOSCH Serie 6 Waschmaschine Frontansicht")))
                .andExpect(jsonPath(prefix + ".productAttributes[0].attributeName", is("energieeffizienzklasse")))
                .andExpect(jsonPath(prefix + ".productAttributes[0].attributeValue", is("A+++")))
                .andExpect(jsonPath(prefix + ".productAttributes[1].attributeName", is("fassungsvermögen")))
                .andExpect(jsonPath(prefix + ".productAttributes[1].attributeValue", is("9 KG")));
    }

    private void assertProductSamsungPhone(ResultActions actions, String prefix) throws Exception {
        prefix = (prefix == null) ? "$" : prefix;
        actions.andExpect(jsonPath(prefix + ".name", is("Samsung Galaxy S23 Ultra")))
                .andExpect(jsonPath(prefix + ".sku", is(SAMSUNG_SKU)))
                .andExpect(jsonPath(prefix + ".description", is("6.8 Dynamic AMOLED 2X Display, 200MP Hauptkamera, 5000mAh Akku, 12GB RAM, 512GB Speicher")))
                .andExpect(jsonPath(prefix + ".costPrice", is(1399.99)))
                .andExpect(jsonPath(prefix + ".categoryName", is("Smartphones")))
                .andExpect(jsonPath(prefix + ".brandName", is("Samsung")))
                .andExpect(jsonPath(prefix + ".supplierName", is("Samsung Electronics GmbH")))
                .andExpect(jsonPath(prefix + ".supplierContactEmail", is("b2b.support@samsung.de")))
                .andExpect(jsonPath(prefix + ".stocks[0].quantity", is(50)))
                .andExpect(jsonPath(prefix + ".stocks[0].warehouse.address", is("Muenchen-Zentral")))
                .andExpect(jsonPath(prefix + ".images[0].imageUrl", is("https://assets.samsung.com/de/smartphones/galaxy-s23-ultra/images/galaxy-s23-ultra-green.png")))
                .andExpect(jsonPath(prefix + ".images[0].altText", is("Samsung Galaxy S23 Ultra in Botanic Green")))
                .andExpect(jsonPath(prefix + ".productAttributes[0].attributeName", is("color")))
                .andExpect(jsonPath(prefix + ".productAttributes[0].attributeValue", is("Botanic Green")))
                .andExpect(jsonPath(prefix + ".productAttributes[1].attributeName", is("storage")))
                .andExpect(jsonPath(prefix + ".productAttributes[1].attributeValue", is("512GB")));
    }

}
