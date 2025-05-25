package org.inventory.app.service;

import org.inventory.app.dto.PagedResponseDTO;
import org.inventory.app.dto.RoleDTO;
import org.inventory.app.dto.UserDTO;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO getUserById(Long userId);


    void updatePassword(String email, String newPassword);

    PagedResponseDTO<UserDTO> getAllUsers(Pageable pageable);

    void activateUser(Long userId);

    void assignRoleFor(Long userId, RoleDTO role);

    void removeRoleFromUser(Long userId, Long roleId);
}
