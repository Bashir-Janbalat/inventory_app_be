package org.inventory.app.controller;

import org.inventory.app.dto.LoginDTO;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth Controller Tests")
public class AuthControllerTest extends BaseControllerTest {


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        roleRepository.save(new Role("ROLE_ADMIN"));
        roleRepository.save(new Role("ROLE_USER"));
        UserDTO userDTO = UserDTO.builder()
                .name("Test User")
                .username("testuser")
                .email("test@example.com")
                .password("P@assword123")
                .build();
        userService.createUser(userDTO);

        when(jwtTokenProvider.generateToken(any(Authentication.class)))
                .thenReturn("dummy-jwt-token");

    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    @DisplayName("successful login")
    void login() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder().username("testuser").password("P@assword123").build();
        String content = MAPPER.writeValueAsString(loginDTO);
        performPostRequest(BASE_LOGIN_URL, content)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("dummy-jwt-token"));
    }

    @Test
    @DisplayName("login with invalid credentials should fail")
    void loginWithInvalidCredentialsShouldFail() throws Exception {
        LoginDTO loginDTO = LoginDTO.builder().username("notValidUserName").password("notValidPassword").build();
        String content = MAPPER.writeValueAsString(loginDTO);
        performPostRequest(BASE_LOGIN_URL, content)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("signup - success")
    void signup() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("New User")
                .username("newuser")
                .email("newuser@example.com")
                .password("P@assword123")
                .build();
        String content = MAPPER.writeValueAsString(userDTO);
        performPostRequest(BASE_SIGNUP_URL, content)
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("signup - duplicate username")
    void signupDuplicateUsername() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("Duplicate User")
                .username("testuser")
                .email("duplicate@example.com")
                .password("P@assword123")
                .build();
        String content = MAPPER.writeValueAsString(userDTO);
        performPostRequest(BASE_SIGNUP_URL, content)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Username already taken"));
    }
    @Test
    @DisplayName("signup - duplicate email")
    void signupDuplicateEmail() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("Another User")
                .username("anotheruser")
                .email("test@example.com")  // Same email as in setUp()
                .password("P@assword123")
                .build();
        String content = MAPPER.writeValueAsString(userDTO);
        performPostRequest(BASE_SIGNUP_URL, content)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("user with email 'test@example.com' already exists."));
    }
    @Test
    @DisplayName("signup - invalid email format")
    void signupInvalidEmail() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("New User")
                .username("newuser")
                .email("invalid-email")
                .password("P@assword123")
                .build();
        String content = MAPPER.writeValueAsString(userDTO);
        performPostRequest(BASE_SIGNUP_URL, content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }
    @Test
    @DisplayName("signup - empty username")
    void signupEmptyUsername() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("New User")
                .username("")
                .email("user@example.com")
                .password("P@assword123")
                .build();
        String content = MAPPER.writeValueAsString(userDTO);
        performPostRequest(BASE_SIGNUP_URL, content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("signup - weak password")
    void signupWeakPassword() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("New User")
                .username("newuser")
                .email("newuser@example.com")
                .password("123")
                .build();
        String content = MAPPER.writeValueAsString(userDTO);
        performPostRequest(BASE_SIGNUP_URL, content)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }


}
