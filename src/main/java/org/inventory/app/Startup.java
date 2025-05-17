package org.inventory.app;

import lombok.RequiredArgsConstructor;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.inventory.app.repository.RoleRepository;
import org.inventory.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class Startup implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUser();
    }

    public void initUser() {
        if (userRepository.findAll().isEmpty()) {
            Role adminRole = roleRepository.findRoleByName("ROLE_ADMIN")
                    .orElseGet(() -> new Role("ROLE_ADMIN"));

            Role userRole = roleRepository.findRoleByName("ROLE_USER")
                    .orElseGet(() -> new Role("ROLE_USER"));

            Set<Role> roles = Set.of(adminRole, userRole);
            String password = passwordEncoder.encode("Ba%123456789");
            userRepository.save(new User("Bob Bob", "Bob", "Bob@gmail.com", password, roles));
        }
    }

}
