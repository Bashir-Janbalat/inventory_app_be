package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.common.ValueWrapper;
import org.inventory.app.dto.RoleDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.Role;
import org.inventory.app.repository.RoleRepository;
import org.inventory.app.service.RoleService;
import org.inventory.app.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roles")
    public ValueWrapper<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roleDTOS = roleRepository.findAll().stream().map(role -> new RoleDTO(role.getId(), role.getName())).toList();
        log.info("All roles successfully retrieved");
        return new ValueWrapper<>(roleDTOS);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"roles"}, allEntries = true)
    public ValueWrapper<RoleDTO> createRole(String roleName) {
        if (roleRepository.existsByName(roleName)) {
            log.warn("Attempt to create role with an existing name: {}", roleName);
            throw new IllegalArgumentException("Role with name '" + roleName + "' already exists");
        }
        Role role = roleRepository.save(new Role(roleName));
        log.info("Role '{}' successfully created", roleName);
        return new ValueWrapper<>(new RoleDTO(role.getId(), role.getName()));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"roles"}, allEntries = true)
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        userService.removeRoleFromAllUsers(roleId);
        roleRepository.delete(role);
    }
}
