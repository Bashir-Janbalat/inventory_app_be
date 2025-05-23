package org.inventory.app.service;

import org.inventory.app.dto.LoginDTO;
import org.inventory.app.dto.UserDTO;

public interface AuthService {
    String login(LoginDTO loginDto);
    void signup(UserDTO userDto);

    void activateUser(Long userId);
}
