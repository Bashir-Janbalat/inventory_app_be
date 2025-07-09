package org.inventory.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.*;
import org.inventory.app.security.jwt.JwtTokenProvider;
import org.inventory.app.service.AuthService;
import org.inventory.app.service.PasswordResetTokenService;
import org.inventory.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/inventory/api/auth")
@Slf4j
@Tag(name = "Authentication", description = "Endpoints for user authentication and password management")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordResetTokenService passwordResetTokenService;
    private final UserService userService;

    @Operation(summary = "User login", description = "Authenticate user and generate JWT token")
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDTO loginDto) {

        String token = authService.login(loginDto);

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken(token);

        return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
    }

    @Operation(summary = "User registration", description = "Register a new user account")
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid UserDTO userDTO) {
        authService.signup(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "User logout", description = "Invalidate the JWT token to log out user")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        long expiration = jwtTokenProvider.getExpirationFromToken(token) - System.currentTimeMillis();
        jwtTokenProvider.addTokenToBlacklist(token, expiration);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Request password reset link", description = "Send password reset link to user's email")
    @PostMapping("/send-reset-link")
    public ResponseEntity<String> requestReset(@RequestParam @Email String email) {
        passwordResetTokenService.createTokenForUser(email);
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @Operation(summary = "Reset password", description = "Reset user's password using reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid PasswordResetRequestDTO request) {
        Optional<PasswordResetTokenDTO> resetTokenDTO = passwordResetTokenService.validateToken(request.getToken());
        if (resetTokenDTO.isPresent()) {
            userService.updatePassword(resetTokenDTO.get().getEmail(), request.getNewPassword());
            passwordResetTokenService.markTokenAsUsedByToken(request.getToken());
            return ResponseEntity.ok("Password successfully reset.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
    }
}
