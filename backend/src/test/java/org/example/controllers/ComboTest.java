package org.example.controllers;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.builders.ComboTestBuilder;
import org.example.models.Combo;
import org.example.models.ComboProduct;
import org.example.repositories.ComboProductRepository;
import org.example.repositories.ComboRepository;
import org.example.security.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@DisplayName("Combo Controller Test")
class ComboTest extends IntegrationTestBase {

    private static final String COMBO_BASE_URL = "/api/combos";

    private final ComboTestBuilder comboBuilder = new ComboTestBuilder();

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private ComboProductRepository comboProductRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @DisplayName("Test Manager without restaurant ID (Global Checks)")
    @Nested
    class ManagerWithoutRestaurantIdTest {

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForGetComboDetails() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", 1L)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForCreateCombo() {

            given()
                    .log().all()
                    .spec(authSpecInvalidManager())
                    .body(comboBuilder.buildJson())
                    .when()
                    .log().all()
                    .post(COMBO_BASE_URL + "/")
                    .then()
                    .log().all()
                    .statusCode(409);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForPatchCombo() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withName("Updated Name")
                    .withPrice(new BigDecimal("60.00"));

            given()
                    .spec(authSpecInvalidManager())
                    .body(patchBuilder.buildFullPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForDeleteCombo() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("Tests GET /combos/{id}/details should get combo details by id")
    @Nested
    class GetComboDetailsById {

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 200 OK when combo exists")
        void shouldGetComboDetailsById() {
            given()
                    .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Zestaw Klasyczny Duży"))
                    .body("price", comparesEqualTo(new BigDecimal("52.00")))
                    .body("comboProduct", hasSize(greaterThan(0)))
                    .body("comboProduct.quantity", everyItem(greaterThan(0)));
        }

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequestWhenIdIsInvalid() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", "invalid")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {
            when()
                    .get(COMBO_BASE_URL + "/{id}/details", 1L)
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 403 Forbidden when user is employee")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", 1L)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 403 Forbidden when invalid manager tries to access")
        void shouldReturnForbiddenForInvalidManagerAccessingComboById() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", 1L)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("GET /api/combos/{id}/details should return 404 Not Found when combo does not exist")
        void shouldReturnNotFoundWhenComboDoesNotExist() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", 9999L)
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("GET /api/combos/{id}/details should return correct structure with product details")
        void shouldReturnCorrectStructureWithProductDetails() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .get(COMBO_BASE_URL + "/{id}/details", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", notNullValue())
                    .body("price", notNullValue())
                    .body("comboProduct", notNullValue())
                    .body("comboProduct[0].quantity", notNullValue())
                    .body("comboProduct[0].productDto", notNullValue());
        }
    }

    @DisplayName("Tests POST /combos/ to create combo")
    @Nested
    class CreateComboTest {

        @Test
        @DisplayName("POST /api/combos/ should return 200 OK when data is valid")
        void shouldCreateComboSuccessfully() {
            comboBuilder
                .withName("Nowy Zestaw Testowy")
                .withPrice(new BigDecimal("55.00"))
                .clearComponents()
                .withComponent(1L, 2)
                .withComponent(2L, 1);

            given()                    .log().all()

                    .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                    .spec(authSpec(UserRole.MANAGER))
                    .body(comboBuilder.buildJson())
                    .when()                    .log().all()

                    .post(COMBO_BASE_URL + "/")
                    .then()                    .log().all()

                    .statusCode(200)
                    .body("name", equalTo("Nowy Zestaw Testowy"))
                    .body("price", comparesEqualTo(new BigDecimal("55.00")))
                    .body("comboProduct", hasSize(2));
        }

        @Test
        @DisplayName("POST /api/combos/ should return 400 Bad Request when name is blank")
        void shouldReturnBadRequestWhenNameIsBlank() {
            comboBuilder
                    .withName("");

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(comboBuilder.buildJson())
                    .when()
                    .post(COMBO_BASE_URL + "/")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 400 Bad Request when price is negative or zero")
        void shouldReturnBadRequestWhenPriceIsInvalid() {
            comboBuilder
                    .withPrice(new BigDecimal("0.00"));

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(comboBuilder.buildJson())
                    .when()
                    .post(COMBO_BASE_URL + "/")
                    .then()
                    .statusCode(400);

            ComboTestBuilder negativePriceCombo = new ComboTestBuilder()
                    .withPrice(new BigDecimal("-10.00"));

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(negativePriceCombo.buildJson())
                    .when()
                    .post(COMBO_BASE_URL + "/")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 400 Bad Request when components list is empty")
        void shouldReturnBadRequestWhenComponentsIsEmpty() {
            comboBuilder
                    .clearComponents();

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(comboBuilder.buildJson())
                    .when()
                    .post(COMBO_BASE_URL + "/")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {

            given()
                    .contentType("application/json")
                    .body(comboBuilder.buildJson())
                    .when()
                    .post(COMBO_BASE_URL + "/")
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 403 Forbidden when user is employee")
        void shouldReturnForbiddenWhenUserIsEmployee() {

            given()                    .log().all()

                    .spec(authSpec(UserRole.EMPLOYEE))
                    .body(comboBuilder.buildJson())
                    .when()                    .log().all()

                    .post(COMBO_BASE_URL + "/")
                    .then()                    .log().all()

                    .statusCode(403);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 409 Conflict when invalid manager tries to create combo")
        void shouldReturnConflictForInvalidManager() {

            given()                    .log().all()

                    .spec(authSpecInvalidManager())
                    .body(comboBuilder.buildJson())
                    .when()                    .log().all()

                    .post(COMBO_BASE_URL + "/")
                    .then()                    .log().all()

                    .statusCode(409);
        }

        @Test
        @DisplayName("POST /api/combos/ should return 404 Not Found when product in components does not exist")
        void shouldReturnNotFoundWhenProductDoesNotExist() {
            comboBuilder
                    .clearComponents()
                    .withComponent(99999L, 1);

            given()                    .log().all()

                    .spec(authSpec(UserRole.MANAGER))
                    .body(comboBuilder.buildJson())
                    .when()                    .log().all()

                    .post(COMBO_BASE_URL + "/")
                    .then()                    .log().all()

                    .statusCode(404);
        }

        @Test
        @DisplayName("POST /api/combos/ should create combo with multiple components")
        void shouldCreateComboWithMultipleComponents() {
            comboBuilder
                    .withName("Mega Zestaw")
                    .withPrice(new BigDecimal("75.00"))
                    .clearComponents()
                    .withComponent(1L, 1)
                    .withComponent(11L, 2)
                    .withComponent(17L, 1);

            given()                    .log().all()

                    .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                    .spec(authSpec(UserRole.MANAGER))
                    .body(comboBuilder.buildJson())
                    .when()                    .log().all()

                    .post(COMBO_BASE_URL + "/")
                    .then()                    .log().all()

                    .statusCode(200)
                    .body("name", equalTo("Mega Zestaw"))
                    .body("price", comparesEqualTo(new BigDecimal("75.00")))
                    .body("comboProduct", hasSize(3));
        }
    }

    @DisplayName("Tests PATCH /combos/{id} to update combo")
    @Nested
    class PatchComboTest {

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 200 OK and update all fields (full update)")
        void shouldPatchComboFullUpdateSuccessfully() {
            // Given: Combo ID 1 exists with original values
            Optional<Combo> originalCombo = comboRepository.findWithDetailsById(1L);
            assertThat(originalCombo).isPresent();
            int originalComboProductCount = originalCombo.get().getComboProducts().size();
            assertThat(originalComboProductCount).isGreaterThan(0);

            // Get original ComboProduct IDs to verify they are replaced
            Set<Long> originalComboProductIds = originalCombo.get().getComboProducts().stream()
                    .map(ComboProduct::getId)
                    .collect(java.util.stream.Collectors.toSet());

            // Create patch request with new values and different components
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withName("Zaktualizowany Zestaw Klasyczny")
                    .withPrice(new BigDecimal("65.00"))
                    .clearComponents()
                    .withComponent(1L, 2)  // Different product and quantity
                    .withComponent(11L, 1);

            // When & Then: Perform PATCH request
            given()
                    .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildFullPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Zaktualizowany Zestaw Klasyczny"))
                    .body("price", comparesEqualTo(new BigDecimal("65.00")))
                    .body("comboProduct", hasSize(2));

            // Verify database state
            Optional<Combo> updatedCombo = comboRepository.findWithDetailsById(1L);
            assertThat(updatedCombo).isPresent();
            assertThat(updatedCombo.get().getName()).isEqualTo("Zaktualizowany Zestaw Klasyczny");
            assertThat(updatedCombo.get().getPrice()).isEqualByComparingTo(new BigDecimal("65.00"));
            assertThat(updatedCombo.get().getComboProducts()).hasSize(2);

            // Verify orphanRemoval: old ComboProducts should be deleted
            Set<Long> newComboProductIds = updatedCombo.get().getComboProducts().stream()
                    .map(ComboProduct::getId)
                    .collect(java.util.stream.Collectors.toSet());

            // New IDs should be different from original IDs (orphanRemoval replaced them)
            assertThat(newComboProductIds).doesNotContainAnyElementsOf(originalComboProductIds);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should update only price when only price is provided (partial update)")
        void shouldPatchComboPartialUpdateOnlyPrice() {
            // Given: Get original combo state
            Optional<Combo> originalCombo = comboRepository.findWithDetailsById(1L);
            assertThat(originalCombo).isPresent();
            String originalName = originalCombo.get().getName();
            int originalProductsCount = originalCombo.get().getComboProducts().size();

            // Create patch request with only price
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("99.99"));

            // When & Then: Perform PATCH request with only price
            given()
                    .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("price", comparesEqualTo(new BigDecimal("99.99")));

            // Verify database state - name and products should remain unchanged
            Optional<Combo> updatedCombo = comboRepository.findWithDetailsById(1L);
            assertThat(updatedCombo).isPresent();
            assertThat(updatedCombo.get().getName()).isEqualTo(originalName);
            assertThat(updatedCombo.get().getPrice()).isEqualByComparingTo(new BigDecimal("99.99"));
            assertThat(updatedCombo.get().getComboProducts()).hasSize(originalProductsCount);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should update only name when only name is provided")
        void shouldPatchComboPartialUpdateOnlyName() {
            // Given: Get original combo state
            Optional<Combo> originalCombo = comboRepository.findWithDetailsById(1L);
            assertThat(originalCombo).isPresent();
            BigDecimal originalPrice = originalCombo.get().getPrice();
            int originalProductsCount = originalCombo.get().getComboProducts().size();

            // Create patch request with only name
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withName("Nowa Nazwa Zestawu");

            // When & Then
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildNameOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Nowa Nazwa Zestawu"));

            // Verify database state
            Optional<Combo> updatedCombo = comboRepository.findWithDetailsById(1L);
            assertThat(updatedCombo).isPresent();
            assertThat(updatedCombo.get().getName()).isEqualTo("Nowa Nazwa Zestawu");
            assertThat(updatedCombo.get().getPrice()).isEqualByComparingTo(originalPrice);
            assertThat(updatedCombo.get().getComboProducts()).hasSize(originalProductsCount);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("60.00"));

            given()
                    .contentType("application/json")
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 403 Forbidden when user is employee")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("60.00"));

            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 403 Forbidden when manager tries to access combo from different restaurant (cross-tenant)")
        void shouldReturnForbiddenForCrossTenantAccess() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("60.00"));

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("60.00"));

            given()
                    .spec(authSpecInvalidManager())
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 404 Not Found when combo does not exist")
        void shouldReturnNotFoundWhenComboDoesNotExist() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("60.00"));

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 99999L)
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 400 Bad Request when price is negative")
        void shouldReturnBadRequestWhenPriceIsNegative() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("-10.00"));

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 400 Bad Request when price is zero")
        void shouldReturnBadRequestWhenPriceIsZero() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withPrice(new BigDecimal("0.00"));

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildPriceOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 400 Bad Request when name is blank")
        void shouldReturnBadRequestWhenNameIsBlank() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withName("");

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildNameOnlyPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/combos/{id} should return 404 Not Found when product in components does not exist")
        void shouldReturnNotFoundWhenProductInComponentsDoesNotExist() {
            ComboTestBuilder patchBuilder = new ComboTestBuilder()
                    .withName("Test Combo")
                    .withPrice(new BigDecimal("50.00"))
                    .clearComponents()
                    .withComponent(99999L, 1);

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchBuilder.buildFullPatchJson())
                    .when()
                    .patch(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("Tests DELETE /combos/{id} to delete combo")
    @Nested
    class DeleteComboTest {

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 200 OK and perform soft delete")
        void shouldDeleteComboSuccessfullySoftDelete() {
            // Given: Combo ID 4 exists (Zestaw Goliata)
            Optional<Combo> comboBeforeDelete = comboRepository.findWithDetailsById(4L);
            assertThat(comboBeforeDelete).isPresent();
            assertThat(comboBeforeDelete.get().getDeletedAt()).isNull();

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 4L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Zestaw Goliata"));

            // Then: Verify soft delete - combo should not be found by normal query (due to @SQLRestriction)
            Optional<Combo> comboAfterDelete = comboRepository.findWithDetailsById(4L);
            assertThat(comboAfterDelete).isEmpty();

            // Verify deleted_at is set using native query (bypassing @SQLRestriction)
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM combos WHERE id = ? AND deleted_at IS NOT NULL",
                    Integer.class, 4L);
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should verify deleted_at timestamp is set after deletion")
        void shouldVerifyDeletedAtIsSetAfterDeletion() {
            // Given: Combo ID 3 exists
            Optional<Combo> comboBeforeDelete = comboRepository.findById(3L);
            assertThat(comboBeforeDelete).isPresent();

            // When: Delete the combo
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 3L)
                    .then()
                    .statusCode(200);

            // Then: Verify using native query that deleted_at is set
            java.sql.Timestamp deletedAt = jdbcTemplate.queryForObject(
                    "SELECT deleted_at FROM combos WHERE id = ?",
                    java.sql.Timestamp.class, 3L);
            assertThat(deletedAt).isNotNull();
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should cascade delete ComboProduct entries (orphanRemoval)")
        void shouldCascadeDeleteComboProducts() {
            // Given: Combo ID 2 exists with ComboProducts
            Optional<Combo> comboBeforeDelete = comboRepository.findWithDetailsById(2L);
            assertThat(comboBeforeDelete).isPresent();
            Set<Long> comboProductIds = comboBeforeDelete.get().getComboProducts().stream()
                    .map(ComboProduct::getId)
                    .collect(java.util.stream.Collectors.toSet());
            assertThat(comboProductIds).isNotEmpty();

            // When: Delete the combo
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 2L)
                    .then()
                    .statusCode(200);

            // Then: Verify ComboProducts are deleted from database
            for (Long comboProductId : comboProductIds) {
                Integer count = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM combo_product WHERE id = ?",
                        Integer.class, comboProductId);
                assertThat(count).isEqualTo(0);
            }
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenNotLoggedIn() {
            when()
                    .delete(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 403 Forbidden when user is employee")
        void shouldReturnForbiddenWhenUserIsEmployee() {
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 403 Forbidden when manager tries to delete combo from different restaurant (cross-tenant)")
        void shouldReturnForbiddenForCrossTenantAccess() {
            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 1L)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("DELETE /api/combos/{id} should return 404 Not Found when combo does not exist")
        void shouldReturnNotFoundWhenComboDoesNotExist() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(COMBO_BASE_URL + "/{id}", 99999L)
                    .then()
                    .statusCode(404);
        }
    }
}
