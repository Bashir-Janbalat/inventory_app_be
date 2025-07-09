package org.inventory.app.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.RoleDTO;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.service.RoleService;
import org.inventory.app.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/inventory/api/users")
@Slf4j
@Tag(name = "Users", description = "Operations related to user and role management")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;


    @Operation(summary = "Get all users with pagination (USER_MANAGEMENT or USER_VIEW role required)")
    @GetMapping
    @PreAuthorize("hasAnyRole('USER_VIEW', 'USER_MANAGEMENT')")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok().body(users);
    }

    @Operation(summary = "Activate a user account (USER_MANAGEMENT role required)")
    @PostMapping("/activate/{userId}")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<String> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok("User activated successfully");
    }

    @Operation(summary = "Get all roles (USER_MANAGEMENT or USER_VIEW role required)")
    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('USER_VIEW', 'USER_MANAGEMENT')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok().body(roleService.getAllRoles().getValue());
    }

    @Operation(summary = "Assign a role to a user (USER_MANAGEMENT role required)")
    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<Void> assignRole(@PathVariable Long userId, @RequestBody @Valid RoleDTO role) {
        userService.assignRoleFor(userId, role);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Create a new role (USER_MANAGEMENT role required)")
    @PostMapping("/roles")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<RoleDTO> createRole(@RequestBody @Valid RoleDTO role) {
        ValueWrapper<RoleDTO> roleDTO = roleService.createRole(role.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDTO.getValue());
    }

    @Operation(summary = "Remove a role from a user (USER_MANAGEMENT role required)")
    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<Void> removeRoleFromUser(@PathVariable Long userId, @PathVariable Long roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a role from all users and system (USER_MANAGEMENT role required)")
    @DeleteMapping("/roles/{roleId}")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<Void> removeRoleFromAllUsers(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

}
