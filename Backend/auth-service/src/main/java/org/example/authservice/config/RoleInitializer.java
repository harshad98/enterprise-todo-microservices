package org.example.authservice.config;

import lombok.RequiredArgsConstructor;
import org.example.authservice.entity.Role;
import org.example.authservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

//when application starts,
//(ROLE_USER, ROLE_ADMIN) roles will be automatically added in the database
@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<String> defaultRoles = List.of(
                "ROLE_USER",
                "ROLE_ADMIN"
        );

        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByName(roleName)) {
                Role role = Role.builder()
                        .name(roleName)
                        .build();

                roleRepository.save(role);
            }
        }
    }
}
