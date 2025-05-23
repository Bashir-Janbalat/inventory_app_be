package org.inventory.app.service;

import org.inventory.app.dto.PasswordResetTokenDTO;
import org.inventory.app.model.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenService {

    PasswordResetToken createTokenForUser(String email);

    Optional<PasswordResetTokenDTO> validateToken(String token);


    void markTokenAsUsedByToken(String token);
}


