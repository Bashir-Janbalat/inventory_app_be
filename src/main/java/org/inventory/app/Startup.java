package org.inventory.app;

import lombok.RequiredArgsConstructor;
import org.inventory.app.model.Role;
import org.inventory.app.model.User;
import org.inventory.app.repository.RoleRepository;
import org.inventory.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class Startup implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initUser();
    }

    public void initUser() {
        if (userRepository.findAll().isEmpty() && roleRepository.findAll().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(new Role("ROLE_ADMIN"));
            roles.add(new Role("ROLE_USER"));
            String password = passwordEncoder.encode("Ba%123456789");
            userRepository.save(new User("Bob Bob", "Bob", "Bob@gmail.com", password, roles));
        }
    }

}
