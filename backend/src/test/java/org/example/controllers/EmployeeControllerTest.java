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

@DisplayName("Employee Controller Tests")
public class EmployeeControllerTest extends IntegrationTestBase {

    private static final String EMPLOYEES_URL = "/api/employees";

    @Nested
    @DisplayName("POST /api/employees")
    class CreateEmployeeTests {

        @Test
        @DisplayName("should return 201 when manager has restaurant")
        void createEmployee_Success() {
            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(OnboardingTestBuilder.employee().buildMap())
            .when()
                .post(EMPLOYEES_URL)
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("email", equalTo("newemployee@test.com"))
                .body("role", equalTo("EMPLOYEE"));
        }

        @Test
        @DisplayName("should return 409 when email already exists")
        void createEmployee_Conflict() {
            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(OnboardingTestBuilder.employee()
                        .withEmail("employee@email")
                        .buildMap())
            .when()
                .post(EMPLOYEES_URL)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("should return 409 when manager has no restaurant")
        void createEmployee_ConflictWhenNoRestaurant() {
            given()
                .spec(authSpecInvalidManager())
                .body(OnboardingTestBuilder.employee()
                        .withEmail("orphan@test.com")
                        .buildMap())
            .when()
                .post(EMPLOYEES_URL)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("should return 403 when user is EMPLOYEE")
        void createEmployee_ForbiddenForEmployee() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(OnboardingTestBuilder.employee()
                        .withEmail("another@test.com")
                        .buildMap())
            .when()
                .post(EMPLOYEES_URL)
            .then()
                .statusCode(403);
        }
    }
}
