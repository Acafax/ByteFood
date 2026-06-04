package org.example.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.builders.StockItemDictionaryBuilder;
import org.example.models.StockItemDictionary;
import org.example.repositories.StockItemDictionariesRepository;
import org.example.security.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@Slf4j
@DisplayName("StockItemDictionary Integration Tests")
public class StockItemDictionaryTest extends IntegrationTestBase {

    private static final String BASE_URL = "/api/stock-item-dictionaries";

    private final StockItemDictionaryBuilder builder = new StockItemDictionaryBuilder();

    @Autowired
    private StockItemDictionariesRepository stockItemDictionariesRepository;

    // ==================== CREATE ====================

    @DisplayName("Test POST /api/stock-item-dictionaries - Create StockItemDictionary")
    @Nested
    class CreateStockItemDictionaryTest {

        @Test
        @DisplayName("POST - Should create StockItemDictionary and return 201 Created")
        void shouldCreateStockItemDictionary() {
            Map<String, Object> newItem = builder
                    .withName("Nowy Karton Sałaty")
                    .withPrice(new BigDecimal("70.00"))
                    .withUnit("KG")
                    .withMultipleOfSemiProduct(new BigDecimal("5000"))
                    .withSemiProductID(14L)
                    .buildMap();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(newItem)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(201)
                .body("name", equalTo("Nowy Karton Sałaty"))
                .body("price", notNullValue())
                .body("unit", equalTo("KG"))
                .body("id", notNullValue());

            // Verify in database
            List<StockItemDictionary> all = stockItemDictionariesRepository.findAll();
            boolean exists = all.stream().anyMatch(item -> "Nowy Karton Sałaty".equals(item.getName()));
            Assertions.assertTrue(exists, "StockItemDictionary should be saved in database");
        }

        @Test
        @DisplayName("POST - Should return 400 Bad Request when name is blank")
        void shouldReturnBadRequestWhenNameIsBlank() {
            Map<String, Object> invalidItem = builder
                    .withName("")
                    .buildMap();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(invalidItem)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("POST - Should return 400 Bad Request when price is negative")
        void shouldReturnBadRequestWhenPriceIsNegative() {
            Map<String, Object> invalidItem = builder
                    .withName("Invalid Price Item")
                    .withPrice(new BigDecimal("-10.00"))
                    .buildMap();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(invalidItem)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("POST - Should return 400 Bad Request when multipleOfSemiProduct is negative")
        void shouldReturnBadRequestWhenMultipleIsNegative() {
            Map<String, Object> invalidItem = builder
                    .withName("Invalid Multiple Item")
                    .withMultipleOfSemiProduct(new BigDecimal("-100"))
                    .buildMap();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(invalidItem)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("POST - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {
            Map<String, Object> item = builder.buildMap();

            given()
                .contentType("application/json")
                .body(item)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("POST - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenEmployee() {
            Map<String, Object> item = builder
                    .withName("Employee Item")
                    .buildMap();

            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(item)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("POST - Should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            Map<String, Object> item = builder
                    .withName("No Restaurant Item")
                    .buildMap();

            given()
                .spec(authSpecInvalidManager())
                .body(item)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("POST - Should return 404 Not Found when semiProductID does not exist")
        void shouldReturnNotFoundWhenSemiProductDoesNotExist() {
            Map<String, Object> item = builder
                    .withName("NonExistent SemiProduct Item")
                    .withSemiProductID(99999L)
                    .buildMap();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(item)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("POST - Should return 403 Forbidden when creating with semiProductID from another restaurant (cross-tenant injection) and NOT create in DB")
        void shouldReturnForbiddenWhenUsingSemiProductFromAnotherRestaurant() {
            Map<String, Object> item = builder
                    .withName("Cross Tenant Stock Item")
                    .withSemiProductID(1L) // semiProductId from restaurant 1
                    .buildMap();

            long countBefore = stockItemDictionariesRepository.count();

            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
                .body(item)
            .when()
                .post(BASE_URL)
            .then()
                .statusCode(403);

            // Verification of the absence of side effects in DB
            long countAfter = stockItemDictionariesRepository.count();
            Assertions.assertEquals(countBefore, countAfter, "No new stock item dictionary should be created in DB after cross-tenant injection attempt");
        }
    }

    // ==================== GET ====================

    @DisplayName("Test GET /api/stock-item-dictionaries - Get StockItemDictionaries")
    @Nested
    class GetStockItemDictionaryTest {

        @Test
        @DisplayName("GET / - Should return 200 OK with list of stock item dictionaries")
        void shouldReturnListOfStockItemDictionaries() {
            given()
                    .log().all()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                    .log().all()
                .get(BASE_URL)
            .then()
                    .log().all()
                .statusCode(200)
                .body("size()", equalTo(11))
                .body("name", hasItems(
                        "Sałata Lodowa (Luz - 1KG)",
                        "Bułka Brioche (Sztuka)",
                        "Wołowina Mielona (Luz 1KG)",
                        "Ketchup Butelka 1L"
                ));
        }

        @Test
        @DisplayName("GET / - Should return 200 OK with empty list for restaurant without stock item dictionaries")
        void shouldReturnEmptyListForEmptyRestaurant() {
            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .get(BASE_URL)
            .then()
                .statusCode(200)
                .body("$", is(List.of()));
        }

        @Test
        @DisplayName("GET /{id} - Should return 200 OK with stock item dictionary details")
        void shouldReturnStockItemDictionaryById() {
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .get(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(200)
                .body("id", equalTo(100))
                .body("name", equalTo("Sałata Lodowa (Luz - 1KG)"))
                .body("unit", equalTo("KG"));
        }

        @Test
        @DisplayName("GET /{id} - Should return 404 Not Found when stock item dictionary does not exist")
        void shouldReturnNotFoundForNonExistentId() {
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .get(BASE_URL + "/{id}", 99999L)
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("GET / - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedForGetAll() {
            when()
                .get(BASE_URL)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("GET /{id} - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedForGetById() {
            when()
                .get(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("GET / - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenForEmployee() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .get(BASE_URL)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("GET /{id} - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenForEmployeeGetById() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .get(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("GET / - Should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForManagerWithoutRestaurant() {
            given()
                .spec(authSpecInvalidManager())
            .when()
                .get(BASE_URL)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("GET /{id} - Should return 403 Forbidden when manager from different restaurant tries to access")
        void shouldReturnForbiddenWhenManagerFromDifferentRestaurant() {
            given()
                    .log().all()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                    .log().all()
                .get(BASE_URL + "/{id}", 100L)
            .then()
                    .log().all()
                .statusCode(403);
        }
    }

    // ==================== PATCH ====================

    @DisplayName("Test PATCH /api/stock-item-dictionaries/{id} - Patch StockItemDictionary")
    @Nested
    class PatchStockItemDictionaryTest {

        @Test
        @DisplayName("PATCH /{id} - Should patch name successfully and verify change in database")
        void shouldPatchNameSuccessfully() throws IOException {
            String patchPayload = Files.readString(ResourceUtils.getFile(
                    "classpath:stockItemDictionaryTestCases/patchStockItemDictionaryName.json").toPath());

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchPayload)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(200)
                .body("name", equalTo("Zaktualizowana Sałata Lodowa"))
                .body("id", equalTo(100));

            // Verify change in database
            Optional<StockItemDictionary> updated = stockItemDictionariesRepository.findById(100L);
            Assertions.assertTrue(updated.isPresent());
            Assertions.assertEquals("Zaktualizowana Sałata Lodowa", updated.get().getName());
        }

        @Test
        @DisplayName("PATCH /{id} - Should patch price successfully using JSON file")
        void shouldPatchPriceSuccessfully() throws IOException {
            String patchPayload = Files.readString(ResourceUtils.getFile(
                    "classpath:stockItemDictionaryTestCases/patchStockItemDictionaryPrice.json").toPath());

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchPayload)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(200)
                .body("id", equalTo(100));

            // Verify change in database
            Optional<StockItemDictionary> updated = stockItemDictionariesRepository.findById(100L);
            Assertions.assertTrue(updated.isPresent());
            Assertions.assertEquals(0, new BigDecimal("15.50").compareTo(updated.get().getPrice()));
        }

        @Test
        @DisplayName("PATCH /{id} - Should patch using builder and verify only provided fields changed")
        void shouldPatchOnlyProvidedFieldsUsingBuilder() {
            // Get original values
            Optional<StockItemDictionary> original = stockItemDictionariesRepository.findById(200L);
            Assertions.assertTrue(original.isPresent());
            BigDecimal originalPrice = original.get().getPrice();

            String newName = "Zaktualizowana Bułka Brioche";
            Map<String, Object> patchData = Map.of("name", newName);

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 200L)
            .then()
                .statusCode(200)
                .body("name", equalTo(newName));

            // Verify only name changed, price remained
            Optional<StockItemDictionary> updated = stockItemDictionariesRepository.findById(200L);
            Assertions.assertTrue(updated.isPresent());
            Assertions.assertEquals(newName, updated.get().getName());
            Assertions.assertEquals(0, originalPrice.compareTo(updated.get().getPrice()));
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 400 Bad Request when all fields are null (empty body)")
        void shouldReturnBadRequestWhenAllFieldsAreNull() {
            Map<String, Object> emptyPatch = Map.of();

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(emptyPatch)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 400 Bad Request when name is blank")
        void shouldReturnBadRequestWhenNameIsBlank() {
            Map<String, Object> patchData = Map.of("name", "");

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .contentType("application/json")
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenEmployee() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 404 Not Found when stock item dictionary does not exist")
        void shouldReturnNotFoundWhenDoesNotExist() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 99999L)
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 403 and NOT change DB when manager from different restaurant tries to patch")
        void shouldReturnForbiddenWhenManagerFromDifferentRestaurant() {
            Map<String, Object> patchData = Map.of("name", "NEW NAME");

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(patchData)
                    .when()
                    .patch(BASE_URL + "/{id}", 100L)
                    .then()
                    .statusCode(403);


            Optional<StockItemDictionary> entity = stockItemDictionariesRepository.findById(100L);
            Assertions.assertTrue(entity.isPresent());
            Assertions.assertNotEquals("NEW NAME", entity.get().getName());
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            Map<String, Object> patchData = Map.of("name", "Updated Name");

            given()
                .spec(authSpecInvalidManager())
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 403 Forbidden when patching with semiProductID from another restaurant (cross-tenant injection)")
        void shouldReturnForbiddenForCrossTenantSemiProductInjection() {
            Map<String, Object> patchData = Map.of("semiProductID", 1L); // semiProductId from restaurant 1

            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(403);

            // Verification of the absence of side effects in DB
            Optional<StockItemDictionary> entity = stockItemDictionariesRepository.findById(100L);
            Assertions.assertTrue(entity.isPresent());
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 400 Bad Request when price is negative")
        void shouldReturnBadRequestWhenPriceIsNegative() {
            Map<String, Object> patchData = Map.of("price", new BigDecimal("-10.00"));

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /{id} - Should return 400 Bad Request when multipleOfSemiProduct is negative")
        void shouldReturnBadRequestWhenMultipleOfSemiProductIsNegative() {
            Map<String, Object> patchData = Map.of("multipleOfSemiProduct", new BigDecimal("-100"));

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(patchData)
            .when()
                .patch(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(400);
        }
    }

    // ==================== DELETE ====================

    @DisplayName("Test DELETE /api/stock-item-dictionaries/{id} - Delete StockItemDictionary")
    @Nested
    class DeleteStockItemDictionaryTest {

        @Test
        @DisplayName("DELETE /{id} - Should delete stock item dictionary successfully (soft delete)")
        void shouldDeleteStockItemDictionarySuccessfully() {
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .delete(BASE_URL + "/{id}", 402L)
            .then()
                .statusCode(200)
                .body("id", equalTo(402))
                .body("name", equalTo("Zgrzewka Ketchup (6x1L)"));

            // Verify soft delete - record should not be found by normal query (due to @SQLRestriction)
            Optional<StockItemDictionary> afterDelete = stockItemDictionariesRepository.findById(402L);
            Assertions.assertFalse(afterDelete.isPresent(), "StockItemDictionary should be soft deleted");
        }

        @Test
        @DisplayName("DELETE /{id} - Should verify soft delete does not physically remove record")
        void shouldVerifySoftDeleteKeepsRecordInDB() {
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .delete(BASE_URL + "/{id}", 401L)
            .then()
                .statusCode(200)
                .body("name", equalTo("Ketchup Butelka 1L"));

            // JPA should not find it (soft deleted)
            Optional<StockItemDictionary> afterDelete = stockItemDictionariesRepository.findById(401L);
            Assertions.assertTrue(afterDelete.isEmpty(), "Repository should not find soft deleted item");
        }

        @Test
        @DisplayName("DELETE /{id} - Should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {
            when()
                .delete(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("DELETE /{id} - Should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenWhenEmployee() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .delete(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("DELETE /{id} - Should return 404 Not Found when stock item dictionary does not exist")
        void shouldReturnNotFoundWhenDoesNotExist() {
            given()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                .delete(BASE_URL + "/{id}", 99999L)
            .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("DELETE /{id} - Should return 403 when manager from different restaurant tries to delete")
        void shouldReturnForbiddenWhenManagerFromDifferentRestaurant() {
            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .delete(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(403);

            Optional<StockItemDictionary> updated = stockItemDictionariesRepository.findById(100L);
            Assertions.assertTrue(updated.isPresent());
        }

        @Test
        @DisplayName("DELETE /{id} - Should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            given()
                .spec(authSpecInvalidManager())
            .when()
                .delete(BASE_URL + "/{id}", 100L)
            .then()
                .statusCode(409);
        }
    }
}

