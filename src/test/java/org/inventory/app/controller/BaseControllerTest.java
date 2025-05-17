package org.inventory.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inventory.app.dto.BrandDTO;
import org.inventory.app.dto.CategoryDTO;
import org.inventory.app.dto.SupplierDTO;
import org.inventory.app.dto.WarehouseDTO;
import org.inventory.app.repository.*;
import org.inventory.app.security.jwt.JwtTokenProvider;
import org.inventory.app.service.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest()
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    @AfterEach
    public void baseTeardown() {
        databaseCleaner.clean();
    }
    protected static final String BASE_URL_PRODUCTS = "/api/products";
    protected static final String BASE_URL_BRANDS = "/api/brands";
    protected static final String BASE_URL_SUPPLIERS = "/api/suppliers";
    protected static final String BASE_URL_CATEGORIES = "/api/categories";
    protected static final String BASE_LOGIN_URL = "/api/auth/login";
    protected static final String BASE_SIGNUP_URL = "/api/auth/signup";
    protected static final String BASE_URL_PURCHASES = "/api/purchases";
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    @Autowired
    protected DatabaseCleaner databaseCleaner;
    @Autowired
    protected ProductService productService;
    @Autowired
    protected CategoryService categoryService;
    @Autowired
    protected BrandService brandService;
    @Autowired
    protected SupplierService supplierService;
    @Autowired
    protected UserService userService;
    @Autowired
    protected WarehouseService warehouseService;
    @Autowired
    AttributeRepository attributeRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    ProductAttributeRepository productAttributeRepository;
    @Autowired
    StockRepository stockRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    protected ProductRepository productRepository;
    @Autowired
    protected BrandRepository brandRepository;
    @Autowired
    protected SupplierRepository supplierRepository;
    @Autowired
    protected CategoryRepository categoryRepository;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;
    @Autowired
    protected WarehouseRepository warehouseRepository;
    @Autowired
    protected StockMovementRepository stockMovementRepository;

    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;


    protected ResultActions performGetRequest(String url, Object... params) throws Exception {
        return mockMvc.perform(get(String.format(url, params))
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performPostRequest(String url, String content) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    protected ResultActions performPutRequest(String url, String content, Object... params) throws Exception {
        return mockMvc.perform(put(String.format(url, params))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }
    protected ResultActions performPutRequestWithParams(String url, Object... uriVarsAndQuery) throws Exception {
        return mockMvc.perform(put(String.format(url, uriVarsAndQuery))
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected ResultActions performDeleteRequest(String url, Object... params) throws Exception {
        return mockMvc.perform(delete(String.format(url, params))
                .contentType(MediaType.APPLICATION_JSON));
    }

    protected WarehouseDTO createWarehouse(String name, String address) {
        return warehouseService.createWarehouse(new WarehouseDTO(name, address));
    }

    protected CategoryDTO createCategory(String name) {
        return categoryService.createCategory(new CategoryDTO(name));
    }

    protected BrandDTO createBrand(String name) {
        return brandService.createBrand(new BrandDTO(name));
    }

    protected SupplierDTO createSupplier(String name, String email) {
        return supplierService.createSupplier(new SupplierDTO(name, email));
    }

}

