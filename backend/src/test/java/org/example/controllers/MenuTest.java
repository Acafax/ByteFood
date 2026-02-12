package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.security.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@DisplayName("Menu Controller Tests")
public class MenuTest extends IntegrationTestBase {

    private final static String MENU_URL = "/api/menu";


    @DisplayName("Get Menu Tests")
    @Nested
    class GetMenuTests{

        @Test
        @DisplayName("GET /api/menu should return 200 OK when user is manager")
        void getMenu_Success() {
            given()
                .log().all()
                .spec(authSpec(UserRole.MANAGER))
            .when()                .log().all()

                    .get(MENU_URL)
            .then()
                .log().all()
                .body("menuItems", notNullValue())
                .body("modificationTemplates", notNullValue())
                .statusCode(200);

        }

        @Test
        @DisplayName("GET /api/menu should return 409 Conflict when manager has no restaurant")
        void getMenu_Conflict() {
            given()
                .spec(authSpecInvalidManager())
            .when()
                .get(MENU_URL)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("GET /api/menu should return 200 OK with empty lists when restaurant has no products")
        void getEmptyMenu() {
            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .get(MENU_URL)
            .then()
                .body("menuItems", hasSize(0))
                .body("modificationTemplates", hasSize(0))
                .statusCode(200);
        }

        @Test
        @DisplayName("GET /api/menu should return 401 Unauthorized when user is not logged in")
        void getMenu_Unauthorized() {
            given()
            .when()
                .get(MENU_URL)
            .then()
                .statusCode(401);
        }

    }
}