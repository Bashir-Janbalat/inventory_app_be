package org.inventory.app.service;

import org.inventory.app.dto.UserDTO;
import org.inventory.app.model.User;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    User getUserById(Long userId);

    void save(User user);

    void updatePassword(String email, String newPassword);
}
