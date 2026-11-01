package org.example.authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.LoginResponse;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.entity.Role;
import org.example.authservice.entity.User;
import org.example.authservice.repository.RoleRepository;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


//handles business logic for
//user registration, login authentication, password encryption and JWT token generation
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public String register(String username, String email, String rawPassword) {

        if (username == null || username.isBlank()
                || email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username, email and password are required"
            );
        }

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_USER not found. Check RoleInitializer."
                ));

        User user = User.builder()
                .username(username.trim())
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .roles(new HashSet<>(java.util.Set.of(userRole)))
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException(
                        "Invalid username or password"
                ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid username or password"
            );
        }

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtService.generateToken(
                user.getId(),
                roles
        );

        return new LoginResponse(
                user.getId(),
                user.getUsername(),
                token
        );
    }

    @Transactional
    public String registerAdmin(String username, String email, String rawPassword) {

        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists"
            );
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN not found"
                ));

        User admin = User.builder()
                .username(username.trim())
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .roles(new HashSet<>(java.util.Set.of(adminRole)))
                .build();

        userRepository.save(admin);

        return "Admin registered successfully";
    }
}