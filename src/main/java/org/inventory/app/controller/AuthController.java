package org.inventory.app.controller;

import lombok.AllArgsConstructor;
import org.inventory.app.dto.AuthResponseDto;
import org.inventory.app.dto.LoginDTO;
import org.inventory.app.dto.UserDTO;
import org.inventory.app.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDTO loginDto) {

        String token = authService.login(loginDto);

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);

        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserDTO userDTO) {
        authService.signup(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
