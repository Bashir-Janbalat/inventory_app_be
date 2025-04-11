package org.inventory.app.service;

import org.inventory.app.dto.LoginDto;

public interface AuthService {
    String login(LoginDto loginDto);
}
