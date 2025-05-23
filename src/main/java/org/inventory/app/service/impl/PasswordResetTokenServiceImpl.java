package org.inventory.app.service.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.dto.PasswordResetTokenDTO;
import org.inventory.app.exception.ResourceNotFoundException;
import org.inventory.app.model.PasswordResetToken;
import org.inventory.app.model.User;
import org.inventory.app.repository.PasswordResetTokenRepository;
import org.inventory.app.repository.UserRepository;
import org.inventory.app.security.jwt.JwtTokenProvider;
import org.inventory.app.service.EmailService;
import org.inventory.app.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {


    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${domain-to-rest-password}")
    private String domainToRestPassword;

    @Override
    @Transactional
    public PasswordResetToken createTokenForUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        String jwtToken = jwtTokenProvider.generatePasswordResetToken(user.getEmail());
        String resetLink = domainToRestPassword + "?token=" + jwtToken;
        PasswordResetToken token = PasswordResetToken.builder()
                .token(jwtToken)
                .user(user)
                .used(false)
                .build();
        emailService.sendHtmlMail(
                user.getEmail(),
                "Reset Your Password",
                "<p>Hello " + user.getName() + ",</p>" +
                "<p>Click the following link to reset your password:</p>" +
                "<p><a href=\"" + resetLink + "\">Reset Password</a></p>" +
                "<p>If you didn't request this, please ignore this email.</p>"
        );
        log.info("Generated password reset link for user {}", user.getEmail());
        return passwordResetTokenRepository.save(token);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PasswordResetTokenDTO> validateToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .filter(t -> jwtTokenProvider.validatePasswordResetToken(t.getToken()) && !t.isUsed())
                .map(t -> new PasswordResetTokenDTO(t.getUser().getEmail()));

    }

    @Override
    @Transactional
    public void markTokenAsUsedByToken(String token) {
        passwordResetTokenRepository.findByToken(token).ifPresent(t -> {
            t.setUsed(true);
            passwordResetTokenRepository.save(t);
        });
    }
}
