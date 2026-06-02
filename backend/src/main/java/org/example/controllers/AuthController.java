package org.example.controllers;

import jakarta.validation.Valid;
import org.example.config.AppSecurityProperties;
import org.example.dtos.LoginRequest;
import org.example.dtos.LoginResponse;
import org.example.dtos.RegisterRequest;
import org.example.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AppSecurityProperties appSecurityProperties;

    public AuthController(AuthService authService, AppSecurityProperties appSecurityProperties) {
        this.authService = authService;
        this.appSecurityProperties = appSecurityProperties;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/register")
    ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (!appSecurityProperties.isRegistrationEnabled()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        LoginResponse registrationResponse = authService.register(registerRequest);
        return ResponseEntity.ok().body(registrationResponse);
    }
}
