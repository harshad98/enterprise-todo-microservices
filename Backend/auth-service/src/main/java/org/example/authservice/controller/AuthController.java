package org.example.authservice.controller;
import lombok.RequiredArgsConstructor;
import org.example.authservice.dto.LoginRequest;
import org.example.authservice.dto.LoginResponse;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.service.AuthService;
import org.springframework.web.bind.annotation.*;

//receives the auth requests from client and uses authservice logic
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
//this is just testing for github
    private final AuthService authService;

    @PostMapping("/register")
    public String register(@RequestBody java.util.Map<String, String> request) {

        return authService.register(
                request.get("username"),
                request.get("email"),
                request.get("password")
        );
    }
    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request) {

        return authService.login(request);
    }

    @PostMapping("/register-admin")
    public String registerAdmin(
            @RequestBody java.util.Map<String, String> request) {

        return authService.registerAdmin(
                request.get("username"),
                request.get("email"),
                request.get("password")
        );
    }
}