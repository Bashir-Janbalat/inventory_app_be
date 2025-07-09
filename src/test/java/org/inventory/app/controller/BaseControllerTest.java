package org.inventory.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

    protected static final String BASE_URL_PRODUCTS = "/inventory/api/products";
    protected static final String BASE_URL_BRANDS = "/inventory/api/brands";
    protected static final String BASE_URL_SUPPLIERS = "/inventory/api/suppliers";
    protected static final String BASE_URL_CATEGORIES = "/inventory/api/categories";
    protected static final String BASE_LOGIN_URL = "/inventory/api/auth/login";
    protected static final String BASE_SIGNUP_URL = "/inventory/api/auth/signup";
    protected static final String BASE_URL_PURCHASES = "/inventory/api/purchases";
    protected static final String BASE_URL_USERS = "/inventory/api/users";
    protected static final ObjectMapper MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

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


    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    protected EmailService emailService;
    @MockitoBean
    protected PasswordResetTokenService passwordResetTokenService;


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

