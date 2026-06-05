package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.user.LoginRequest;
import org.example.dtos.user.LoginResponse;
import org.example.dtos.user.RegisterRequest;
import org.example.dtos.user.UserInformation;
import org.example.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService ) {
        this.authService = authService;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/register")
    ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse registrationResponse = authService.register(registerRequest);
        return ResponseEntity.ok().body(registrationResponse);
    }

    @GetMapping("/me")
    ResponseEntity<UserInformation> getUserInformation() {
        UserInformation userInformation = authService.getCurrentUserInformation();
        return ResponseEntity.ok().body(userInformation);
    }
}
