package org.example.dtos.onboarding;

import jakarta.validation.constraints.NotBlank;

public record CreateRestaurantOnboardingRequest(
        @NotBlank(message = "Restaurant name cannot be blank")
        String restaurantName,

        @NotBlank(message = "Stock name cannot be blank")
        String stockName
) {
}
