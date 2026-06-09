package org.example.controllers;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.models.ModificationTemplate;
import org.example.repositories.ModificationTemplateRepository;
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
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

@Slf4j
@DisplayName("Modification integration test")
public class ModificationTest extends IntegrationTestBase {

    private static final String MODIFICATION_URL= "/api/modifications";

    @Autowired
    ModificationTemplateRepository modificationTemplateRepository;

    @Autowired
    EntityManager entityManager;

    @DisplayName("Test Manager without restaurant ID (Global Checks)")
    @Nested
    class ManagerWithoutRestaurantIdTest {

        @Test
        @DisplayName("GET /api/modifications/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForGetModificationById() {
            given()
                    .spec(authSpecInvalidManager())
            .when()
                    .get(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("POST /api/modifications should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForCreateModification() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationRequest.json").toPath());

            given()
                    .spec(authSpecInvalidManager())
                    .body(testModification)
            .when()
                    .post(MODIFICATION_URL)
            .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForPatchModification() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .spec(authSpecInvalidManager())
                    .body(patchModification)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForDeleteModification() {
            given()
                    .spec(authSpecInvalidManager())
            .when()
                    .delete(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(409);
        }
    }

    @DisplayName("GET /api/modifications/{id} get modification by id")
    @Nested
    class getModificationsById{
        @Test
        @DisplayName("GET /api/modifications/{id} should return 200 OK when modification exists")
        void shouldGetModificationById() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .get(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Dodatkowy ser Cheddar"))
                    .body("subcategory.subcategoryName", equalTo("cheeses"));
        }

        @Test
        @DisplayName("GET /api/modifications/{id} should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequest(){
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .get(MODIFICATION_URL + "/{id}", "numer")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("GET /api/modifications/{id} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized(){
            when()
                    .get(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/modifications/{id} should return 403 Forbidden when user is not manager")
        void shouldReturnForbidden() {
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                    .get(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("GET /api/modifications/{id} should return 403 Forbidden when manager from different restaurant")
        void shouldReturnForbiddenIfDifferentRestaurantResource() {
            given()
                    .log().all()
                    .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                    .log().all()
                    .get(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .log().all()
                    .statusCode(403);
        }

        @Test
        @DisplayName("GET /api/modifications/{id} should return 404 Not Found when modification does not exist")
        void shouldReturnNotFound(){
            given()
                    .log().all()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .log().all()
                    .get(MODIFICATION_URL + "/{id}", 1000L)
            .then()
                    .log().all()
                    .statusCode(404);
        }
    }

    @DisplayName("Test POST /api/modifications to create modification")
    @Nested
    class createModificationTest {

        @Test
        @DisplayName("POST /api/modifications should return 201 Created when data is valid")
        void shouldCreateModification() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationRequest.json").toPath());

            given()
                    .log().all()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(testModification)
            .when()
                    .log().all()
                    .post(MODIFICATION_URL)
            .then()
                    .log().all()
                    .statusCode(201)
                    .body("name", equalTo("Testowa modyfikacja"))
                    .body("subcategory.subcategoryName", equalTo("buns"))
                    .body("price", equalTo(5.0f));
        }

        @Test
        @DisplayName("POST /api/modifications should return 201 Created when data is valid")
        void shouldCreateModificationWithoutSemi() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationWithoutSemiRequest.json").toPath());

            given()
                    .log().all()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(testModification)
                    .when()
                    .log().all()
                    .post(MODIFICATION_URL)
                    .then()
                    .log().all()
                    .statusCode(201)
                    .body("name", equalTo("Testowa modyfikacja"))
                    .body("subcategory.subcategoryName", equalTo("buns"))
                    .body("price", equalTo(5.0f));
        }

        @Test
        @DisplayName("POST /api/modifications should return 400 Bad Request when validation fails")
        void shouldReturnBadRequestWhenInvalidData() throws IOException {
            String invalidModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createInvalidModificationRequest.json").toPath());

            given().log().all()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(invalidModification)
            .when()
                    .post(MODIFICATION_URL)
            .then().log().all()
                    .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/modifications should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationRequest.json").toPath());

            given()
                    .body(testModification)
            .when()
                    .post(MODIFICATION_URL)
            .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("POST /api/modifications should return 403 Forbidden when user is not manager")
        void shouldReturnForbidden() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .body(testModification)
            .when()
                    .post(MODIFICATION_URL)
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("POST /api/modifications should return 404 Not Found when semi product does not exist")
        void shouldReturnNotFoundWhenSemiProductNotExist() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationWithNotExistSemiProductRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(testModification)
            .when()
                    .post(MODIFICATION_URL)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("POST /api/modifications should return 403 Forbidden when creating modification with semiProductId from another restaurant (cross-tenant injection)")
        void shouldReturnForbiddenWhenUsingSemiProductFromAnotherRestaurant() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationWithForeignSemiProduct.json").toPath());

            long countBefore = modificationTemplateRepository.count();

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(testModification)
            .when()
                    .post(MODIFICATION_URL)
            .then()
                    .statusCode(403);

            // Verification of the absence of side effects in DB
            long countAfter = modificationTemplateRepository.count();
            Assertions.assertEquals(countBefore, countAfter, "No new modification should be created in DB after cross-tenant injection attempt");
        }

        @Test
        @DisplayName("POST /api/modifications should return 403 Forbidden and NOT create modification in DB when user is EMPLOYEE")
        void shouldReturnForbiddenAndNotCreateModificationWhenEmployee() throws IOException {
            String testModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/createModificationRequest.json").toPath());

            long countBefore = modificationTemplateRepository.count();

            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .body(testModification)
            .when()
                    .post(MODIFICATION_URL)
            .then()
                    .statusCode(403);

            // Verification of the absence of side effects in DB
            long countAfter = modificationTemplateRepository.count();
            Assertions.assertEquals(countBefore, countAfter, "No new modification should be created in DB after EMPLOYEE attempt");
        }
    }

    @DisplayName("Test PATCH /api/modifications/{id} to patch modification")
    @Nested
    class patchModificationTest {

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 200 OK when modification successful patched")
        void shouldPatchModification() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchModification)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(200)
                    .body("name", is("Zmieniona nazwa modyfikacji"))
                    .body("subcategory.subcategoryName", is("drinks"))
                    .body("price", is(7.50f))
                    .body("semiProductId", is(10));
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 200 OK when modification successful patched")
        void shouldPatchSemiProductInModification() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationSemiProductRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchModification)
                    .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", is("Dodatkowy ser Cheddar"))
                    .body("subcategory.subcategoryName", is("cheeses"))
                    .body("price", is(4.0f))
                    .body("semiProductId", is(11));
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequest() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchModification)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", "numer")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 401 Unauthorized when user is NOT logged in")
        void shouldReturnUnauthorized() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .body(patchModification)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 403 Forbidden when user is NOT manager")
        void shouldReturnForbidden() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .body(patchModification)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 403 and NOT change DB when manager from different restaurant")
        void shouldReturnForbiddenIfDifferentRestaurantResource() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(patchModification)
                    .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
                    .then()
                    .statusCode(403);


            // Verification of the absence of side effects in the DB
            Optional<ModificationTemplate> entity = modificationTemplateRepository.findById(1L);
            Assertions.assertTrue(entity.isPresent());
            Assertions.assertNotEquals("Zmieniona nazwa modyfikacji", entity.get().getName());
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 404 Not Found when modification NOT exist")
        void shouldReturnNotFound() throws IOException {
            String patchModification = Files.readString(ResourceUtils.getFile("classpath:modificationControllerTestCases/patchModificationRequest.json").toPath());

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchModification)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 404 Not Found when semi product does not exist")
        void shouldReturnNotFoundWhenSemiProductNotExist() {
            String patchWithInvalidSemiProduct = """
                    {
                      "semiProductId": 9999
                    }
                    """;

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchWithInvalidSemiProduct)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 403 Forbidden when patching with semiProductId from another restaurant (cross-tenant injection)")
        void shouldReturnForbiddenForCrossTenantSemiProductInjection() {
            String patchWithForeignSemiProduct = """
                    {
                      "semiProductId": 1
                    }
                    """;

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(patchWithForeignSemiProduct)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(403);

            // Verification of the absence of side effects in DB
            Optional<ModificationTemplate> entity = modificationTemplateRepository.findById(1L);
            Assertions.assertTrue(entity.isPresent());
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 400 Bad Request when price is negative")
        void shouldReturnBadRequestWhenPriceIsNegative() {
            String patchWithNegativePrice = """
                    {
                      "price": -5.00
                    }
                    """;

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchWithNegativePrice)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/modifications/{id} should return 400 Bad Request when name is empty")
        void shouldReturnBadRequestWhenNameIsEmpty() {
            String patchWithEmptyName = """
                    {
                      "name": ""
                    }
                    """;

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(patchWithEmptyName)
            .when()
                    .patch(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(400);
        }
    }

    @DisplayName("Test DELETE /api/modifications/{id} to delete modification")
    @Nested
    class deleteModificationTest {

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 200 OK when modification exists")
        void shouldDeleteModificationById() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .delete(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Dodatkowy ser Cheddar"))
                    .body("subcategory.subcategoryName", equalTo("cheeses"));

            boolean deletedModification = modificationTemplateRepository.existsById(1L);
            Assertions.assertFalse(deletedModification, "Modification should be soft deleted");
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should soft delete modification when modification exists")
        void deletedModificationShouldBeSoftDeleted() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .delete(MODIFICATION_URL + "/{id}", 2L)
            .then()
                    .statusCode(200)
                    .body("name", equalTo("Dodatkowy bekon"))
                    .body("subcategory.subcategoryName", equalTo("addons"));

            Boolean recordExistsInDb = (Boolean) entityManager.createNativeQuery(
                            "SELECT EXISTS (SELECT 1 FROM modification_template WHERE id = :id AND deleted_at IS NOT NULL)")
                    .setParameter("id", 2L)
                    .getSingleResult();

            boolean deletedModification = modificationTemplateRepository.existsById(2L);

            // 1. Record still exist in db (SQL)
            Assertions.assertTrue(recordExistsInDb, "Modification should exist in DB with deleted_at set");

            // 2. Record 'dont exit' (soft deleted) in db (JPA)
            Assertions.assertFalse(deletedModification, "Repository should not find soft deleted modification");
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequest() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .delete(MODIFICATION_URL + "/{id}", "numer")
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized() {
            when()
                    .delete(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 403 Forbidden when user is employee")
        void shouldReturnForbidden() {
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                    .delete(MODIFICATION_URL + "/{id}", 1L)
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 403 and NOT delete from DB when manager from different restaurant")
        void shouldReturnForbiddenIfDifferentRestaurantResourceDelete() {
            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .when()
                    .delete(MODIFICATION_URL + "/{id}", 1L)
                    .then()
                    .statusCode(403);


            // Verification of the absence of side effects in the DB
            Optional<org.example.models.ModificationTemplate> entity = modificationTemplateRepository.findById(1L);
            Assertions.assertTrue(entity.isPresent(), "ModificationTemplate should not be deleted");
        }

        @Test
        @DisplayName("DELETE /api/modifications/{id} should return 404 Not Found when modification does not exist")
        void shouldReturnNotFound() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .delete(MODIFICATION_URL + "/{id}", 1000L)
            .then()
                    .statusCode(404);
        }
    }
}
