package org.inventory.app.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.inventory.app.dto.RoleDTO;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends BaseControllerTest {

    private User testUser;
    private User testUserManagement;

    @BeforeEach
    void setUp() {
        Role roleUserView = roleRepository.save(new Role("ROLE_USER_VIEW"));
        Role roleUserManagement = roleRepository.save(new Role("ROLE_USER_MANAGEMENT"));

        testUser = new User("Test User", "testuser", "test@example.com", "P@ssword123", Set.of(roleUserView));
        userRepository.save(testUser);
        testUserManagement = new User("Test User", "testRoleUserManagement", "testRoleUserManagement@example.com",
                "P@ssword123", Set.of(roleUserManagement));
        userRepository.save(testUserManagement);
    }

    @Test
    @DisplayName("GET /api/users - should return all users")
    void getAllUsers() throws Exception {
        when(jwtTokenProvider.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("Token");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsername(anyString())).thenReturn("testuser");

        performGetRequest(BASE_URL_USERS)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].email", is(testUser.getEmail())))
                .andExpect(jsonPath("$.content[1].email", is(testUserManagement.getEmail())));
    }

    @Test
    @DisplayName("GET /api/users/activate/{userId} - should activate user")
    void activateUser() throws Exception {
        when(jwtTokenProvider.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("Token");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsername(anyString())).thenReturn("testRoleUserManagement");

        performGetRequest(BASE_URL_USERS + "/activate/" + testUser.getId())
                .andExpect(status().isOk())
                .andExpect(content().string("User activated successfully"));
    }

    @Test
    @DisplayName("GET /api/users/roles - should return all roles")
    void getAllRoles() throws Exception {
        when(jwtTokenProvider.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("Token");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsername(anyString())).thenReturn("testuser");

        performGetRequest(BASE_URL_USERS + "/roles")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("POST /api/users/create-role - should create new role")
    void createRole() throws Exception {
        when(jwtTokenProvider.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("Token");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsername(anyString())).thenReturn("testRoleUserManagement");

        String roleName = "ROLE_TEST";
        String content = MAPPER.writeValueAsString(RoleDTO.builder().name(roleName).build());
        performPostRequest(BASE_URL_USERS + "/create-role",content)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(roleName)));
    }

    @Test
    @DisplayName("POST /api/users/assign-role - should assign role to user")
    void assignRoleToUser() throws Exception {
        when(jwtTokenProvider.getTokenFromRequest(any(HttpServletRequest.class))).thenReturn("Token");
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
        when(jwtTokenProvider.getUsername(anyString())).thenReturn("testRoleUserManagement");

        Role role = roleRepository.save(new Role("ROLE_CUSTOM"));
        String content = MAPPER.writeValueAsString(RoleDTO.builder().name(role.getName()).build());

        performPostRequest(BASE_URL_USERS + "/assign-role?userId=" + testUser.getId(),
                content)
                .andExpect(status().isNoContent());
    }
}
