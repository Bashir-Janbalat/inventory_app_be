package org.inventory.app.controller;


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
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER_VIEW', 'USER_MANAGEMENT')")
    public ResponseEntity<PagedResponseDTO<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "asc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponseDTO<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok().body(users);
    }

    @GetMapping("/activate/{userId}")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<String> activateUser(@PathVariable Long userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok("User activated successfully");
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAnyRole('USER_VIEW', 'USER_MANAGEMENT')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok().body(roleService.getAllRoles().getValue());
    }

    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<Void> assignRole(@RequestParam Long userId, @RequestBody RoleDTO role) {
        userService.assignRoleFor(userId, role);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-role")
    @PreAuthorize("hasRole('USER_MANAGEMENT')")
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO role) {
        ValueWrapper<RoleDTO> roleDTO = roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleDTO.getValue());
    }

}
