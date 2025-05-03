package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.exception.AlreadyExistsException;
import org.inventory.app.mapper.UserMapper;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.inventory.app.repository.RoleRepository;
import org.inventory.app.repository.UserRepository;
import org.inventory.app.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            log.warn("Attempt to create user with an existing username: {}", userDTO.getUsername());
            throw new AlreadyExistsException("Username already taken");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            log.warn("Attempt to create user with an existing email: {}", userDTO.getEmail());
            throw new AlreadyExistsException("user", "email", userDTO.getEmail());
        }

        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        Optional<Role> role = roleRepository.findRoleByName("ROLE_USER");
        if (role.isPresent()) {
            user.setRoles(Set.of(role.get()));
            log.info("Assigned 'ROLE_USER' to user: {}", userDTO.getUsername());
        } else {
            log.error("Role 'ROLE_USER' not found, user {} cannot be created", userDTO.getUsername());
            throw new IllegalStateException("Role 'ROLE_USER' not found");
        }

        userRepository.save(user);
        log.info("User with username '{}' and email '{}' successfully created", userDTO.getUsername(), userDTO.getEmail());
    }
}
