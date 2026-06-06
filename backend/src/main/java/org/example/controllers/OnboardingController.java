package org.example.controllers;

import jakarta.validation.Valid;
import org.example.dtos.onboarding.CreateRestaurantOnboardingRequest;
import org.example.dtos.onboarding.RestaurantOnboardingResponse;
import org.example.services.OnboardingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("/restaurant")
    public ResponseEntity<RestaurantOnboardingResponse> createRestaurant(
            @Valid @RequestBody CreateRestaurantOnboardingRequest request
    ) {
        RestaurantOnboardingResponse response = onboardingService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
