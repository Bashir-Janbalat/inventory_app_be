package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.RoleDTO;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.mapper.UserMapper;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.inventory.app.repository.RoleRepository;
import org.inventory.app.repository.UserRepository;
import org.inventory.app.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    @CacheEvict(value = {"users", "user"}, allEntries = true)
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            log.warn("Attempt to create user with an existing username: {}", userDTO.getUsername());
            throw new AlreadyExistsException("Username already taken");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Attempt to create user with an existing email: {}", userDTO.getEmail());
            throw new AlreadyExistsException("user", "email", userDTO.getEmail());
        }

        User user = userMapper.toEntityNewUser(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Optional<Role> role = roleRepository.findRoleByName("ROLE_USER");
        if (role.isPresent()) {
            user.setRoles(Set.of(role.get()));
            log.info("Assigned 'ROLE_USER' to user: {}", userDTO.getUsername());
        } else {
            log.error("Role 'ROLE_USER' not found, user {} cannot be created", userDTO.getUsername());
            throw new IllegalStateException("Role 'ROLE_USER' not found");
        }

        User saved = userRepository.save(user);
        log.info("User with username '{}' and email '{}' successfully created", userDTO.getUsername(), userDTO.getEmail());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "#userId")
    public UserDTO getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        log.info("Fetched user with ID: {}", user.getId());
        return userMapper.toDto(user);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"users", "user"}, allEntries = true)
    public void updatePassword(String email, String newPassword) {
        log.info("Attempting to update password for user with email '{}'", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user '{}'", email);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "'page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
    public PagedResponseDTO<UserDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        log.info("Fetched {} users from DB (page {} size {}) (and cached in 'users') ",
                users.getContent().size(), pageable.getPageNumber(), pageable.getPageSize());
        return new PagedResponseDTO<>(users.map(userMapper::toDto));
    }

    @Override
    @Transactional
    @CacheEvict(value = {"users", "user"}, allEntries = true)
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        user.setActive(true);
        userRepository.save(user);
        log.info("User with ID {} activated successfully", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"users", "user"}, allEntries = true)
    public void assignRoleFor(Long userId, RoleDTO roleDTO) {
        String roleName = roleDTO.getName();
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        Role role = roleRepository.findRoleByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role with name " + roleName + " not found"));
        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Role {} successfully assigned to user {}", role, user.getUsername());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"users", "user"}, allEntries = true)
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + roleId + " not found"));

        user.getRoles().remove(role);
        log.info("Role {} successfully removed from user {}", role, user.getUsername());
        userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"users", "user"}, allEntries = true)
    public void removeRoleFromAllUsers(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + roleId + " not found"));
        List<User> usersWithRole = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .toList();

        usersWithRole.forEach(user -> {
            user.getRoles().remove(role);
            userRepository.save(user);
            log.info("Role {} removed from user {}", role.getName(), user.getUsername());
        });

        log.info("Role {} removed from {} users", role.getName(), usersWithRole.size());
    }
}
