package org.inventory.app.service.impl;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.LoginDTO;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.security.jwt.JwtTokenProvider;
import org.inventory.app.service.AuthService;
import org.inventory.app.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Override
    public String login(LoginDTO loginDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    @Transactional
    public void signup(UserDTO userDto) {
        userService.createUser(userDto);
    }
}
