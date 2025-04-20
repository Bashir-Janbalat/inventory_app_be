package org.inventory.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.inventory.app.repository.*;
import org.inventory.app.security.jwt.JwtTokenProvider;
import org.inventory.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest(properties = "spring.config.name=application-test")
@AutoConfigureMockMvc
public abstract class BaseControllerTest {

    protected static final String BASE_URL_PRODUCTS = "/api/products";
    protected static final String BASE_URL_BRANDS = "/api/brands";
    protected static final String BASE_URL_SUPPLIERS = "/api/suppliers";
    protected static final String BASE_URL_CATEGORIES = "/api/categories";
    protected static final String BASE_LOGIN_URL = "/api/auth/login";
    protected static final String BASE_SIGNUP_URL = "/api/auth/signup";
    protected static final ObjectMapper MAPPER = new ObjectMapper();

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
    protected MockMvc mockMvc;
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

    protected ResultActions performDeleteRequest(String url, Object... params) throws Exception {
        return mockMvc.perform(delete(String.format(url, params))
                .contentType(MediaType.APPLICATION_JSON));
    }
}

