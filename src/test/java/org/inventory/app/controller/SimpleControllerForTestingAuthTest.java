package org.inventory.app.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.util.Sets;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Simple controller For Testing Auth ")
class SimpleControllerForTestingAuthTest extends BaseControllerTest {

    public static final String VALID_TOKEN = "valid_token";
    public static final String TEST_ADMIN = "TEST_ADMIN";
    public static final String TEST_USER = "TEST_USER";
    public static final String ACCESS_DENIED_MESSAGE = "You don't have permission to access this resource.";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        mockJwt("invalid_token", false, "inValidUser");

        createUserWithRole(TEST_ADMIN, "ROLE_ADMIN");
        createUserWithRole(TEST_USER, "ROLE_USER");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.flush();
        roleRepository.flush();
    }

    @Test
    @DisplayName("admin endpoint - authorized")
    void adminEndpointAuthorized() throws Exception {
        mockJwt(VALID_TOKEN, true, TEST_ADMIN);
        performGetRequest("/api/admin")
                .andExpect(status().isOk())
                .andExpect(content().string("Hello Admin"));

    }

    @Test
    @DisplayName("admin endpoint - forbidden for user")
    void adminEndpointUnauthorized() throws Exception {
        mockJwt(VALID_TOKEN, true, TEST_USER);
        performGetRequest("/api/admin")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(ACCESS_DENIED_MESSAGE));
    }

    @Test
    @DisplayName("user endpoint - authorized")
    void userEndpointAuthorized() throws Exception {
        mockJwt(VALID_TOKEN, true, TEST_USER);
        performGetRequest("/api/user")
                .andExpect(status().isOk())
                .andExpect(content().string("Hello User"));
    }

    @Test
    @DisplayName("user endpoint - unauthorized")
    void userEndpointUnauthorized() throws Exception {
        performGetRequest("/api/user")
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("admin endpoint - unauthorized when token is invalid")
    void adminEndpointUnauthorizedWithInvalidToken() throws Exception {
        mockJwtWithException(VALID_TOKEN, ExpiredJwtException.class);
        performGetRequest("/api/admin")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Token expired. Please login again."));
    }

    @Test
    @DisplayName("user endpoint - Forbidden for admin")
    void userEndpointForbiddenForAdmin() throws Exception {
        mockJwt(VALID_TOKEN, true, TEST_ADMIN);
        performGetRequest("/api/user")
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You don't have permission to access this resource."));
    }

    @Test
    @DisplayName("admin endpoint - should reject missing token")
    void adminEndpointRejectsMissingToken() throws Exception {
        when(jwtTokenProvider.getTokenFromRequest(any())).thenReturn(null);

        performGetRequest("/api/admin")
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("user endpoint - should handle invalid token")
    void userEndpointHandlesExpiredToken() throws Exception {
        mockJwtWithException(VALID_TOKEN, JwtException.class);
        performGetRequest("/api/user")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("JWT token is invalid.")));
    }

    private void createUserWithRole(String username, String roleName) {
        Set<Role> roles = Sets.set(new Role(roleName));
        userRepository.save(new User("name", username, username + "@example.com", "P@assword123", roles));
    }

    private void mockJwt(String token, boolean valid, String username) {
        when(jwtTokenProvider.getTokenFromRequest(any())).thenReturn(token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(valid);
        when(jwtTokenProvider.getUsername(token)).thenReturn(username);
    }

    private void mockJwtWithException(String token, Class<? extends RuntimeException> ex) {
        when(jwtTokenProvider.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn(token);
        when(jwtTokenProvider.validateToken(anyString())).thenThrow(ex);
    }
}
