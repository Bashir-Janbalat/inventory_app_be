package org.inventory.app.mapper;

import org.inventory.app.dto.RoleDTO;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setActive(userDTO.isActive());
        Set<Role> roles = userDTO.getRolesDTO().stream()
                .map(roleDTO -> new Role(roleDTO.getId(), roleDTO.getName())).collect(Collectors.toSet());
        user.setRoles(roles);
        return user;
    }

    public UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setActive(user.isActive());
        Set<RoleDTO> roles = user.getRoles().stream()
                .map(role -> new RoleDTO(role.getId(), role.getName())).collect(Collectors.toSet());
        userDTO.setRolesDTO(roles);
        return userDTO;
    }

    public User toEntityNewUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.getId());
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }
}
