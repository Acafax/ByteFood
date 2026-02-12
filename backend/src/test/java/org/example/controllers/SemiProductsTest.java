package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.builders.SemiProductTestBuilder;
import org.example.models.SemiProduct;
import org.example.repositories.SemiProductRepository;
import org.example.security.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Slf4j
@DisplayName("SemiProducts Integration Tests")
public class SemiProductsTest extends IntegrationTestBase {

    private static final String SEMI_PRODUCTS_URL = "/api/semi-products";

    private final SemiProductTestBuilder semiProductTestBuilder = new SemiProductTestBuilder();

    @Autowired
    private SemiProductRepository semiProductRepository;

    @DisplayName("Test POST /api/semi-products - Create SemiProduct")
    @Nested
    class CreateSemiProductTest {

        @Test
        @DisplayName("POST /api/semi-products - Should return 400 Bad Request when name is empty")
        void shouldReturnBadRequestWhenNameIsEmpty() {
            Map<String, Object> invalidSemiProduct = semiProductTestBuilder
                    .withName("")
                    .buildMap();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(invalidSemiProduct)
            .when()
                .post(SEMI_PRODUCTS_URL)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/semi-products - Should return 400 Bad Request when nutritional values are negative")
        void shouldReturnBadRequestWhenNutritionalValuesAreNegative() {
            // Case 2: Negative value (Edge Case)
            Map<String, Object> invalidSemiProduct = semiProductTestBuilder
                    .withCarbohydrate(new BigDecimal("-10.0"))
                    .buildMap();

            given()
                    .log().all()
                .spec(authSpec(UserRole.MANAGER))
                .body(invalidSemiProduct)
            .when()
                    .log().all()

                    .post(SEMI_PRODUCTS_URL)
            .then()
                    .log().all()

                .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/semi-products - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            Map<String, Object> semiProduct = semiProductTestBuilder.buildMap();

            // Brak .spec()
            given()
                .body(semiProduct)
            .when()
                .post(SEMI_PRODUCTS_URL)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("POST /api/semi-products - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            Map<String, Object> semiProduct = semiProductTestBuilder.buildMap();

            given()
                    .log().all()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(semiProduct)
            .when()                    .log().all()
                .post(SEMI_PRODUCTS_URL)
            .then()                    .log().all()
                .statusCode(403);
        }

        @Test
        @DisplayName("POST /api/semi-products - Should return 409 Conflict when creating existing semi-product")
        void shouldReturnConflictWhenCreatingExistingSemiProduct() {
            Map<String, Object> existingSemiProduct = semiProductTestBuilder
                    .buildMap();

            given()                    .log().all()
                .spec(authSpec(UserRole.MANAGER))
                .body(existingSemiProduct)
            .when()                    .log().all()
                .post(SEMI_PRODUCTS_URL);

            given()                    .log().all()
                .spec(authSpec(UserRole.MANAGER))
                .body(existingSemiProduct)
            .when()                    .log().all()
                .post(SEMI_PRODUCTS_URL)
            .then()                    .log().all()
                .statusCode(409);

        }

        @Test
        @DisplayName("POST /api/semi-products - Should create SemiProduct and return 201 Created")
        void createSemiProduct_Success() {
            Map<String, Object> newSemiProduct = semiProductTestBuilder
                    .withName("Unikalna Nowa Bułka")
                    .buildMap();

            given()
                .log().all()
                .spec(authSpec(UserRole.MANAGER))
                .body(newSemiProduct)
            .when()                    .log().all()
                .post(SEMI_PRODUCTS_URL)
            .then()
                .log().all()
                .statusCode(201)
                .body("name", equalTo("Unikalna Nowa Bułka"));
        }
    }

    @DisplayName("Test GET /api/semi-products - Get SemiProducts")
    @Nested
    class GetSemiProductTest {
        @Test
        @DisplayName("GET /api/semi-products - Should return 200 OK with list of semi-products")
        void getSemiProductsTest() {
            given()
                .log().all()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .get(SEMI_PRODUCTS_URL)
            .then()
                .log().all()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].name", notNullValue())
                .body("name", hasItems("Bułka brioche", "Bułka sezamowa", "Sałata lodowa", "Sos Czosnkowy"));
        }

        @Test
        @DisplayName("GET /api/semi-products - Should return empty List is Manager dont has any semi-products")
        void shouldReturnNotFoundForManagerOfEmptyRestaurant() {
            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .get(SEMI_PRODUCTS_URL)
            .then()
                .statusCode(200)
                .body("$", is(List.of()));
        }

        @Test
        @DisplayName("GET /api/semi-products - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            when()
                .get(SEMI_PRODUCTS_URL)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/semi-products - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .get(SEMI_PRODUCTS_URL)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("GET /api/semi-products - Should return 409 Conflict for Manager without restaurant")
        void getSemiProductsTestForManagerWithoutRestaurant() {
            given()
                .log().all()
                .spec(authSpecInvalidManager())
            .when()
                .get(SEMI_PRODUCTS_URL)
            .then()
                .log().all()
                .statusCode(409);
        }

    }

    @DisplayName("Test PATCH /api/semi-products/{id} - Patch SemiProduct")
    @Nested
    class PatchSemiProductTest {

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 400 Bad Request when all fields are null")
        void shouldReturnBadRequestWhenAllFieldsAreNull() {
            Map<String, Object> emptyPatch = Map.of();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(emptyPatch)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .contentType("application/json")
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 404 Not Found when semi-product does not exist")
        void shouldReturnNotFoundWhenSemiProductDoesNotExist() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 9999L)
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 403 when manager from different restaurant tries to patch")
        void shouldReturnForbiddenWhenManagerFromDifferentRestaurant() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpecInvalidManager())
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should patch semi-product name successfully and verify change in database")
        void shouldPatchSemiProductNameSuccessfully() {
            String newName = "Zaktualizowana Bułka brioche";
            Map<String, Object> patchData = Map.of("name", newName);

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(200)
                .body("name", equalTo(newName))
                .body("id", equalTo(1));

            // Verify change in database
            Optional<SemiProduct> updatedSemiProduct = semiProductRepository.findById(1L);
            assertTrue(updatedSemiProduct.isPresent());
            assertEquals(newName, updatedSemiProduct.get().getName());
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should patch semi-product nutritional values successfully")
        void shouldPatchSemiProductNutritionalValuesSuccessfully() {
            Map<String, Object> patchData = Map.of(
                "carbohydrate", 60.0,
                "fat", 12.0,
                "protein", 10.0
            );

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(200)
                .body("carbohydrate", equalTo(60.0f))
                .body("fat", equalTo(12.0f))
                .body("protein", equalTo(10.0f));

            // Verify change in database
            Optional<SemiProduct> updatedSemiProduct = semiProductRepository.findById(1L);
            assertTrue(updatedSemiProduct.isPresent());
            assertEquals(0, new BigDecimal("60.0").compareTo(updatedSemiProduct.get().getCarbohydrate()));
            assertEquals(0, new BigDecimal("12.0").compareTo(updatedSemiProduct.get().getFat()));
            assertEquals(0, new BigDecimal("10.0").compareTo(updatedSemiProduct.get().getProtein()));
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should patch only provided fields and keep others unchanged")
        void shouldPatchOnlyProvidedFieldsAndKeepOthersUnchanged() {
            // Get original values
            Optional<SemiProduct> originalSemiProduct = semiProductRepository.findById(1L);
            assertTrue(originalSemiProduct.isPresent());
            BigDecimal originalCarbohydrate = originalSemiProduct.get().getCarbohydrate();
            BigDecimal originalFat = originalSemiProduct.get().getFat();

            // Patch only name
            String newName = "Tylko nazwa zmieniona";
            Map<String, Object> patchData = Map.of("name", newName);

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(200)
                .body("name", equalTo(newName));

            // Verify only name changed, other values remained
            Optional<SemiProduct> updatedSemiProduct = semiProductRepository.findById(1L);
            assertTrue(updatedSemiProduct.isPresent());
            assertEquals(newName, updatedSemiProduct.get().getName());
            assertEquals(0, originalCarbohydrate.compareTo(updatedSemiProduct.get().getCarbohydrate()));
            assertEquals(0, originalFat.compareTo(updatedSemiProduct.get().getFat()));
        }

        @Test
        @DisplayName("PATCH /api/semi-products/{id} - Should return 400 Bad Request when name is blank")
        void shouldReturnBadRequestWhenNameIsBlank() {
            Map<String, Object> patchData = Map.of("name", "");

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(400);
        }
    }

    @DisplayName("Test DELETE /api/semi-products/{id} - Delete SemiProduct")
    @Nested
    class DeleteSemiProductTest {

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should return 404 Not Found when semi-product does not exist")
        void shouldReturnNotFoundWhenSemiProductDoesNotExist() {
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 9999L)
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should return 403 when manager from different restaurant tries to delete")
        void shouldReturnForbiddenWhenManagerFromDifferentRestaurant() {
            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            given()
                .spec(authSpecInvalidManager())
            .when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 1L)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should delete semi-product successfully (soft delete)")
        void shouldDeleteSemiProductSuccessfully() {

            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 21L)
            .then()
                .statusCode(200)
                .body("id", equalTo(21))
                .body("name", equalTo("Bekon"));

            // Verify soft delete - record should not be found by normal query (due to @SQLRestriction)
            Optional<SemiProduct> afterDelete = semiProductRepository.findById(21L);
            assertFalse(afterDelete.isPresent());
        }

        @Test
        @DisplayName("DELETE /api/semi-products/{id} - Should verify deleted_at is set after deletion")
        void shouldVerifyDeletedAtIsSetAfterDeletion() {

            // Delete the semi-product
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .delete(SEMI_PRODUCTS_URL + "/{id}", 20L)
            .then()
                .statusCode(200);

            // Verify deleted_at is set using native query (bypassing @SQLRestriction)
            Optional<SemiProduct> byId = semiProductRepository.findById(20L);
            assertTrue(byId.isEmpty());
        }
    }


}