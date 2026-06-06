package org.example.dtos.onboarding;

public record RestaurantOnboardingResponse(
        Long restaurantId,
        String restaurantName,
        Long stockId,
        String stockName
) {
}
