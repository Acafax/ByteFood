package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.security.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@DisplayName("Stock Integration Tests")
@Slf4j
public class StockTest extends IntegrationTestBase {

    private static String STOCK_BASE_URL = "/api/stock";


    @DisplayName("Test GET /api/stock - Get Stock Items" )
    @Nested
    class GetStockItemsTest {
        @Test
        @DisplayName("GET /api/stock should return 200 OK when user is manager with valid restaurant")
        void getStockItems_Success() {
            given()
                .log().all()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .get(STOCK_BASE_URL)
            .then()
                .log().all()
                .body("stockItems", notNullValue())
                    .body("stockItems.size()", equalTo(9))
                .statusCode(200);
        }

        @Test
        @DisplayName("GET /api/stock should return 200 OK with empty list when restaurant has no stock items")
        void getEmptyStockListItems_Success() {
            given()
                .log().all()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .get(STOCK_BASE_URL)
            .then()
                .log().all()
                .body("stockItems", equalTo(List.of()))
                .statusCode(200);
        }


        @Test
        @DisplayName("GET /api/stock should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized(){

            when()
                .get(STOCK_BASE_URL)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/stock should return 403 Forbidden when user is not manager")
        void shouldReturnForbidden(){
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .get(STOCK_BASE_URL)
            .then()
                .statusCode(403);
        }


    }


}
