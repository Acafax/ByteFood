package org.example.controllers;

import org.example.IntegrationTestBase;
import org.example.builders.OnboardingTestBuilder;
import org.example.security.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DisplayName("Onboarding Controller Tests")
public class OnboardingTest extends IntegrationTestBase {

    private static final String RESTAURANT_URL = "/api/onboarding/restaurant";
    private static final String AUTH_ME_URL = "/api/auth/me";

    @Nested
    @DisplayName("POST /api/onboarding/restaurant")
    class CreateRestaurantTests {

        @Test
        @DisplayName("should return 201 when manager has no restaurant")
        void createRestaurant_Success() {
            given()
                .spec(authSpecInvalidManager())
                .body(OnboardingTestBuilder.restaurant().buildMap())
            .when()
                .post(RESTAURANT_URL)
            .then()
                .statusCode(201)
                .body("restaurantId", notNullValue())
                .body("restaurantName", equalTo("Test Restaurant"))
                .body("stockId", notNullValue())
                .body("stockName", equalTo("Main Warehouse"));

            given()
                .spec(authSpecInvalidManager())
            .when()
                .get(AUTH_ME_URL)
            .then()
                .statusCode(200)
                .body("restaurantId", notNullValue())
                .body("role", equalTo("MANAGER"));
        }

        @Test
        @DisplayName("should return 403 when employee has no restaurant")
        void createRestaurant_ForbiddenForEmployee() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(OnboardingTestBuilder.restaurant().buildMap())
            .when()
                .post(RESTAURANT_URL)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("should return 403 when manager already has restaurant")
        void createRestaurant_ForbiddenWhenAlreadyAssigned() {
            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(OnboardingTestBuilder.restaurant()
                        .withRestaurantName("Another Restaurant")
                        .withStockName("Another Warehouse")
                        .buildMap())
            .when()
                .post(RESTAURANT_URL)
            .then()
                .statusCode(403);
        }
    }
}
