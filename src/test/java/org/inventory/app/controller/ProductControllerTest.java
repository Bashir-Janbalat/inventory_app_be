package org.inventory.app.controller;

import org.inventory.app.dto.*;
import org.inventory.app.model.Product;
import org.junit.jupiter.api.*;
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

    private BrandDTO samsungBrand;
    private CategoryDTO smartphoneCategory;
    private SupplierDTO samsungSupplier;


    @BeforeEach
    public void setup() {
        cleanDatabase();

        smartphoneCategory = categoryService.createCategory(new CategoryDTO("Smartphones"));
        samsungBrand = brandService.createBrand(new BrandDTO("Samsung"));
        samsungSupplier = supplierService.createSupplier(new SupplierDTO("Samsung Electronics GmbH", "b2b.support@samsung.de"));

        List<ImageDTO> samsungPhoneImages = List.of(
                new ImageDTO("https://assets.samsung.com/de/smartphones/galaxy-s23-ultra/images/galaxy-s23-ultra-green.png",
                        "Samsung Galaxy S23 Ultra in Botanic Green"));
        StockDTO samsungPhoneStock = new StockDTO(50, "Hamburg-Nord");
        List<ProductAttributeDTO> samsungPhoneAttributes = List.of(
                new ProductAttributeDTO("color", "Botanic Green"),
                new ProductAttributeDTO("storage", "512GB"));
        ProductDTO samsungPhone = setReferences(smartphoneCategory, samsungBrand, samsungSupplier, samsungPhoneImages, samsungPhoneStock, samsungPhoneAttributes);
        samsungPhone.setName("Samsung Galaxy S23 Ultra");
        samsungPhone.setSku(SAMSUNG_SKU);
        samsungPhone.setDescription("6.8 Dynamic AMOLED 2X Display, 200MP Hauptkamera, 5000mAh Akku, 12GB RAM, 512GB Speicher");
        samsungPhone.setPrice(BigDecimal.valueOf(1399.99));
        productService.createProduct(samsungPhone);

        CategoryDTO applianceCategory = categoryService.createCategory(new CategoryDTO("Haushaltsgeraete"));
        BrandDTO boschBrand = brandService.createBrand(new BrandDTO("BOSCH"));
        SupplierDTO electronicPartnerSupplier = supplierService.createSupplier(new SupplierDTO("ElectronicPartner Deutschland", "grosshandel@ep-deutschland.de"));

        List<ImageDTO> boschWasherImages = List.of(new ImageDTO("https://media3.bosch-home.com/Product_Shots/1600x900/WAU28S80AT_def.png", "BOSCH Serie 6 Waschmaschine Frontansicht"));
        StockDTO boschWasherStock = new StockDTO(25, "Muenchen-Zentral");
        List<ProductAttributeDTO> boschWasherAttributes = List.of(
                new ProductAttributeDTO("energieeffizienzklasse", "A+++"),
                new ProductAttributeDTO("fassungsvermögen", "9 KG"));
        ProductDTO boschWasher = setReferences(applianceCategory, boschBrand, electronicPartnerSupplier, boschWasherImages, boschWasherStock, boschWasherAttributes);
        boschWasher.setName("BOSCH Serie 6 WAU28S80");
        boschWasher.setSku(BOSCH_SKU);
        boschWasher.setDescription("Waschmaschine, 9 kg, 1400 U/min., EcoSilence Drive, SpeedPerfect, AllergiePlus, Nachlegefunktion");
        boschWasher.setPrice(BigDecimal.valueOf(799.99));
        productService.createProduct(boschWasher);
    }

    @AfterEach
    public void tearDown() {
        try {
            cleanDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cleanDatabase() {
        productAttributeRepository.deleteAll();
        attributeRepository.deleteAll();
        imageRepository.deleteAll();
        stockRepository.deleteAll();
        productRepository.deleteAll();
        brandRepository.deleteAll();
        supplierRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private ProductDTO setReferences(CategoryDTO category, BrandDTO brand, SupplierDTO supplier, List<ImageDTO> images,
                                     StockDTO stock, List<ProductAttributeDTO> productAttributeDTOS) {
        ProductDTO product = new ProductDTO();
        product.setCategoryID(category.getId());
        product.setCategoryName(category.getName());
        product.setBrandID(brand.getId());
        product.setBrandName(brand.getName());
        product.setSupplierID(supplier.getId());
        product.setSupplierName(supplier.getName());
        product.setSupplierContactEmail(supplier.getContactEmail());
        product.setImages(images);
        product.setStock(stock);
        product.setProductAttributes(productAttributeDTOS);
        return product;
    }


    private void assertProductBoschWasher(ResultActions actions, String prefix) throws Exception {
        prefix = (prefix == null) ? "$" : prefix;
        actions.andExpect(jsonPath(prefix + ".name", is("BOSCH Serie 6 WAU28S80")))
                .andExpect(jsonPath(prefix + ".sku", is(BOSCH_SKU)))
                .andExpect(jsonPath(prefix + ".description", is("Waschmaschine, 9 kg, 1400 U/min., EcoSilence Drive, SpeedPerfect, AllergiePlus, Nachlegefunktion")))
                .andExpect(jsonPath(prefix + ".price", is(799.99)))
                .andExpect(jsonPath(prefix + ".categoryName", is("Haushaltsgeraete")))
                .andExpect(jsonPath(prefix + ".brandName", is("BOSCH")))
                .andExpect(jsonPath(prefix + ".supplierName", is("ElectronicPartner Deutschland")))
                .andExpect(jsonPath(prefix + ".supplierContactEmail", is("grosshandel@ep-deutschland.de")))
                .andExpect(jsonPath(prefix + ".stock.quantity", is(25)))
                .andExpect(jsonPath(prefix + ".stock.warehouseLocation", is("Muenchen-Zentral")))
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
                .andExpect(jsonPath(prefix + ".price", is(1399.99)))
                .andExpect(jsonPath(prefix + ".categoryName", is("Smartphones")))
                .andExpect(jsonPath(prefix + ".brandName", is("Samsung")))
                .andExpect(jsonPath(prefix + ".supplierName", is("Samsung Electronics GmbH")))
                .andExpect(jsonPath(prefix + ".supplierContactEmail", is("b2b.support@samsung.de")))
                .andExpect(jsonPath(prefix + ".stock.quantity", is(50)))
                .andExpect(jsonPath(prefix + ".stock.warehouseLocation", is("Hamburg-Nord")))
                .andExpect(jsonPath(prefix + ".images[0].imageUrl", is("https://assets.samsung.com/de/smartphones/galaxy-s23-ultra/images/galaxy-s23-ultra-green.png")))
                .andExpect(jsonPath(prefix + ".images[0].altText", is("Samsung Galaxy S23 Ultra in Botanic Green")))
                .andExpect(jsonPath(prefix + ".productAttributes[0].attributeName", is("color")))
                .andExpect(jsonPath(prefix + ".productAttributes[0].attributeValue", is("Botanic Green")))
                .andExpect(jsonPath(prefix + ".productAttributes[1].attributeName", is("storage")))
                .andExpect(jsonPath(prefix + ".productAttributes[1].attributeValue", is("512GB")));
    }

    @Nested
    @DisplayName("POST /api/products endpoints")
    class CreateProductTests {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should handle invalid Product Data")
        void shouldHandleInvalidProductData() throws Exception {
            performPostRequest(BASE_URL_PRODUCTS, "{}")
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").exists());

        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should create a new product")
        public void createNewLaptopProductShouldReturnCreatedProduct() throws Exception {
            CategoryDTO laptops = categoryService.createCategory(new CategoryDTO("Laptops"));
            BrandDTO apple = brandService.createBrand(new BrandDTO("Apple"));
            SupplierDTO appleDE = supplierService.createSupplier(new SupplierDTO("Apple Deutschland GmbH", "apple.support@apple.de"));

            ProductDTO productDto = ProductDTO.builder()
                    .name("Apple MacBook Pro 16")
                    .sku("MBP16-M2-32GB")
                    .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                    .price(BigDecimal.valueOf(3499.99))
                    .categoryID(laptops.getId())
                    .brandID(apple.getId())
                    .supplierID(appleDE.getId())
                    .images(List.of(ImageDTO.builder()
                            .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                            .altText("MacBook Pro 16 Zoll in Space Grau")
                            .build()))
                    .stock(StockDTO.builder()
                            .quantity(15)
                            .warehouseLocation("Berlin-Mitte")
                            .build())
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
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Apple MacBook Pro 16")))
                    .andExpect(jsonPath("$.sku", is("MBP16-M2-32GB")))
                    .andExpect(jsonPath("$.description", is("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")))
                    .andExpect(jsonPath("$.price", is(3499.99)))
                    .andExpect(jsonPath("$.categoryName", is("Laptops")))
                    .andExpect(jsonPath("$.brandName", is("Apple")))
                    .andExpect(jsonPath("$.supplierName", is("Apple Deutschland GmbH")))
                    .andExpect(jsonPath("$.supplierContactEmail", is("apple.support@apple.de")))
                    .andExpect(jsonPath("$.stock.quantity", is(15)))
                    .andExpect(jsonPath("$.stock.warehouseLocation", is("Berlin-Mitte")))
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
            CategoryDTO laptops = categoryService.createCategory(new CategoryDTO("Laptops"));
            BrandDTO apple = brandService.createBrand(new BrandDTO("Apple"));
            SupplierDTO appleDE = supplierService.createSupplier(
                    new SupplierDTO("Apple Deutschland GmbH", "apple.support@apple.de")
            );

            // 2) Erstes Produkt: Apple MacBook Pro 16
            ProductDTO macBook = ProductDTO.builder()
                    .name("Apple MacBook Pro 16")
                    .sku("MBP16-M2-32GB")
                    .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                    .price(BigDecimal.valueOf(3499.99))
                    .categoryID(laptops.getId())
                    .brandID(apple.getId())
                    .supplierID(appleDE.getId())
                    .images(List.of(
                            ImageDTO.builder()
                                    .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                                    .altText("MacBook Pro 16 Zoll in Space Grau")
                                    .build()
                    ))
                    .stock(StockDTO.builder()
                            .quantity(15)
                            .warehouseLocation("Berlin-Mitte")
                            .build()
                    )
                    .productAttributes(List.of(
                            ProductAttributeDTO.builder().attributeName("processor").attributeValue("Apple M2 Max").build(),
                            ProductAttributeDTO.builder().attributeName("ram").attributeValue("32GB").build(),
                            ProductAttributeDTO.builder().attributeName("storage").attributeValue("1TB SSD").build()
                    ))
                    .build();

            performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(macBook))
                    .andExpect(status().isOk());

            // 3) Zweites Produkt: Dell XPS 13
            BrandDTO dell = brandService.createBrand(new BrandDTO("Dell"));
            SupplierDTO dellDE = supplierService.createSupplier(
                    new SupplierDTO("Dell Germany GmbH", "support@dell.de")
            );

            ProductDTO xps13 = ProductDTO.builder()
                    .name("Dell XPS 13")
                    .sku("XPS13-9300")
                    .description("13 Zoll InfinityEdge Display, Intel i7, 16GB RAM, 512GB SSD")
                    .price(BigDecimal.valueOf(1299.00))
                    .categoryID(laptops.getId())
                    .brandID(dell.getId())
                    .supplierID(dellDE.getId())
                    .images(List.of(
                            ImageDTO.builder()
                                    .imageUrl("https://example.com/dell-xps-13.png")
                                    .altText("Dell XPS 13")
                                    .build()
                    ))
                    .stock(StockDTO.builder()
                            .quantity(10)
                            .warehouseLocation("München")
                            .build()
                    )
                    .productAttributes(List.of(
                            ProductAttributeDTO.builder().attributeName("processor").attributeValue("Intel i7").build(),
                            ProductAttributeDTO.builder().attributeName("ram").attributeValue("16GB").build(),
                            ProductAttributeDTO.builder().attributeName("storage").attributeValue("512GB SSD").build()
                    ))
                    .build();

            performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(xps13))
                    .andExpect(status().isOk());

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
            CategoryDTO laptops = categoryService.createCategory(new CategoryDTO("Laptops"));
            CategoryDTO smartphones = categoryService.createCategory(new CategoryDTO("Smartphones"));

            // Brands und Supplier (wiederverwendbar)
            BrandDTO otto = brandService.createBrand(new BrandDTO("Otto"));
            SupplierDTO appleDE = supplierService.createSupplier(
                    new SupplierDTO("Otto Deutschland GmbH", "Otto.support@apple.de")
            );

            BrandDTO dell = brandService.createBrand(new BrandDTO("Dell"));
            SupplierDTO dellDE = supplierService.createSupplier(
                    new SupplierDTO("Dell Germany GmbH", "support@dell.de")
            );

            // 2) Produkt in Kategorie "Laptops"
            ProductDTO macBook = ProductDTO.builder()
                    .name("Apple MacBook Pro 16")
                    .sku("MBP16-M2-32GB")
                    .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                    .price(BigDecimal.valueOf(3499.99))
                    .categoryID(laptops.getId())
                    .brandID(otto.getId())
                    .supplierID(appleDE.getId())
                    .images(List.of(
                            ImageDTO.builder()
                                    .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                                    .altText("MacBook Pro 16 Zoll in Space Grau")
                                    .build()
                    ))
                    .stock(StockDTO.builder()
                            .quantity(15)
                            .warehouseLocation("Berlin-Mitte")
                            .build()
                    )
                    .productAttributes(List.of(
                            ProductAttributeDTO.builder().attributeName("processor").attributeValue("Apple M2 Max").build(),
                            ProductAttributeDTO.builder().attributeName("ram").attributeValue("32GB").build(),
                            ProductAttributeDTO.builder().attributeName("storage").attributeValue("1TB SSD").build()
                    ))
                    .build();
            performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(macBook))
                    .andExpect(status().isOk());

            // 3) Produkt in Kategorie "Smartphones"
            ProductDTO galaxy = ProductDTO.builder()
                    .name("Samsung Galaxy S21")
                    .sku("SGS21-5G")
                    .description("6.2 Zoll Dynamic AMOLED, Exynos 2100, 8GB RAM, 128GB Speicher")
                    .price(BigDecimal.valueOf(849.00))
                    .categoryID(smartphones.getId())
                    .brandID(samsungBrand.getId())
                    .supplierID(supplierService.createSupplier(
                            new SupplierDTO("Samsung Germany GmbH", "support@samsung.de")
                    ).getId())
                    .images(List.of(
                            ImageDTO.builder()
                                    .imageUrl("https://example.com/galaxy-s21.png")
                                    .altText("Samsung Galaxy S21")
                                    .build()
                    ))
                    .stock(StockDTO.builder()
                            .quantity(20)
                            .warehouseLocation("Hamburg")
                            .build()
                    )
                    .productAttributes(List.of(
                            ProductAttributeDTO.builder().attributeName("display").attributeValue("Dynamic AMOLED").build(),
                            ProductAttributeDTO.builder().attributeName("battery").attributeValue("4000mAh").build()
                    ))
                    .build();
            performPostRequest(BASE_URL_PRODUCTS, MAPPER.writeValueAsString(galaxy))
                    .andExpect(status().isOk());

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


    }

    @Nested
    @DisplayName("GET /api/products/{id}, /api/products endpoints")
    class GetProductTests {

        @Test
        @DisplayName("should return unauthorized without token")
        void shouldReturnUnauthorizedWithoutToken() throws Exception {
            performGetRequest(BASE_URL_PRODUCTS)
                    .andExpect(status().isUnauthorized());
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

            CategoryDTO laptops = categoryService.createCategory(new CategoryDTO("Laptops"));
            BrandDTO apple = brandService.createBrand(new BrandDTO("Apple"));
            SupplierDTO appleDE = supplierService.createSupplier(new SupplierDTO("Apple Deutschland GmbH", "apple.support@apple.de"));

            ProductDTO productDto = ProductDTO.builder()
                    .name("SearchBy Product")
                    .sku("MBP16-M2-32GB")
                    .description("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau")
                    .price(BigDecimal.valueOf(3499.99))
                    .categoryID(laptops.getId())
                    .brandID(apple.getId())
                    .supplierID(appleDE.getId())
                    .images(List.of(ImageDTO.builder()
                            .imageUrl("https://store.apple.com/macbook-pro-16-space-gray.png")
                            .altText("MacBook Pro 16 Zoll in Space Grau")
                            .build()))
                    .stock(StockDTO.builder()
                            .quantity(15)
                            .warehouseLocation("Berlin-Mitte")
                            .build())
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
                    .andExpect(status().isOk());

            performGetRequest(
                    String.format(BASE_URL_PRODUCTS + "?page=0&size=100&searchBy=%s", "SearchBy Product"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].name").value("SearchBy Product"))
                    .andExpect(jsonPath("$.content[0].sku").value("MBP16-M2-32GB"))
                    .andExpect(jsonPath("$.content[0].price").value(3499.99))
                    .andExpect(jsonPath("$.content[0].description").value("16 Zoll Liquid Retina XDR Display, Apple M2 Max Chip, 32GB RAM, 1TB SSD, Space Grau"));
        }
    }


    @Nested
    @DisplayName("PUT /api/products/{id} endpoints")
    class UpdateProductTests {

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
                    .price(BigDecimal.valueOf(1299.99))
                    .categoryID(product.get().getCategory().getId())
                    .brandID(product.get().getBrand().getId())
                    .supplierID(product.get().getSupplier().getId())
                    .build();
            performPutRequest(BASE_URL_PRODUCTS + "/%d", MAPPER.writeValueAsString(updateDto), product.get().getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Samsung Galaxy S23 Ultra Updated")))
                    .andExpect(jsonPath("$.description", is("Updated description")))
                    .andExpect(jsonPath("$.price", is(1299.99)));
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
                    .price(BigDecimal.valueOf(1299.99))
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
    }

    @Nested
    @DisplayName("DELETE /api/products/{id} endpoints")
    class DeleteProductTests {

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should delete existing product")
        void shouldDeleteExistingProduct() throws Exception {
            Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
            if (product.isEmpty()) {
                throw new RuntimeException("Product not found");
            }
            performDeleteRequest(BASE_URL_PRODUCTS + "/%d", product.get().getId())
                    .andExpect(status().isNoContent());

            performGetRequest(BASE_URL_PRODUCTS + "/%d", product.get().getId())
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = {"ADMIN"})
        @DisplayName("should return 404 When deleting non existent product")
        void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
            performDeleteRequest(BASE_URL_PRODUCTS + "/%d", 999)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return unauthorized for delete without token")
        void shouldReturnUnauthorizedForDeleteWithoutToken() throws Exception {
            Optional<Product> product = productRepository.findBySku(SAMSUNG_SKU);
            if (product.isEmpty()) {
                throw new RuntimeException("Product not found");
            }
            performDeleteRequest(BASE_URL_PRODUCTS + "/%d", product.get().getId())
                    .andExpect(status().isUnauthorized());
        }
    }


}
