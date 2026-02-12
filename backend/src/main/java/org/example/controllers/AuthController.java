package org.example.controllers;

import org.example.dtos.LoginRequest;
import org.example.dtos.LoginResponse;
import org.example.dtos.RegisterRequest;
import org.example.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/register")
    ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest registerRequest){
        LoginResponse registrationResponse = authService.register(registerRequest);
        return ResponseEntity.ok().body(registrationResponse);
    }


}
