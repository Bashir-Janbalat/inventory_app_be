package org.inventory.app.service.impl;

import lombok.RequiredArgsConstructor;
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
            throw new AlreadyExistsException("Username already taken");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new AlreadyExistsException("user", "email", userDTO.getEmail());
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Optional<Role> role = roleRepository.findRoleByName("ROLE_USER");
        role.ifPresent(value -> user.setRoles(Set.of(value)));
        userRepository.save(user);
    }
}
