package org.inventory.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.inventory.app.repository.RoleRepository;
import org.inventory.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("!test")
@Slf4j
public class Startup implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (roleRepository.findAll().isEmpty()) {
            Role adminRole = new Role("ROLE_ADMIN");
            Role userRole = new Role("ROLE_USER");
            Role userManagement = new Role("ROLE_USER_MANAGEMENT");
            Role userView = new Role("ROLE_USER_VIEW");
            roleRepository.saveAll(Set.of(adminRole, userRole, userManagement, userView));
            log.info("Initial roles created");
        }

        if (userRepository.findAll().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAll());
            String password = passwordEncoder.encode("Ba%123456789");
            User user = new User("Bob Bob", "Bob", "Bob@gmail.com", password, roles);
            user.setActive(true);
            userRepository.save(user);
            log.info("Initial admin user created");
        }
    }
}
