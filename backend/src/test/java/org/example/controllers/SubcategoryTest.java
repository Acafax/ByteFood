package org.example.controllers;


import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.security.UserRole;
import org.example.repositories.SubcategoryRepository;
import org.example.models.Subcategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


@Slf4j
@DisplayName("SubCategoryController Test")
public class SubcategoryTest extends IntegrationTestBase {

    private static String SUB_CATEGORY_BASE_URL = "/api/subcategory";

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @DisplayName("GET /api/subcategory get restaurant subcategory ")
    @Nested
    class getSubCategory{
        @Test
        void getRestaurantSubCategory(){
            given()
                    .log().all()
                .spec(authSpec(UserRole.MANAGER))
            .when()
                    .log().all()
                    .get(SUB_CATEGORY_BASE_URL)
            .then()
                    .log().all()
                    .body("size()", is(8))
                    .body("subcategory_name",
                            hasItems(
                                "buns",
                                "patties",
                                "cheeses",
                                "vegetables",
                                "addons",
                                "sauces",
                                "drinks",
                                "sides"
                            ))
                    .statusCode(200);
        }

        @Test
        @DisplayName("GET /api/subcategory should return 200 OK when user is ADMIN")
        void shouldAllowAdmin() {
            given()
                    .spec(authSpec(UserRole.ADMIN))
            .when()
                    .get(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(200)
                    .body("size()", is(8))
                    .body("subcategory_name",
                            hasItems(
                                    "buns",
                                    "patties",
                                    "cheeses",
                                    "vegetables",
                                    "addons",
                                    "sauces",
                                    "drinks",
                                    "sides"
                            ));
        }

        @Test
        @DisplayName("GET /api/subcategory should return 200 OK when user is EMPLOYEE")
        void shouldAllowEmployee() {
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                    .get(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(200)
                    .body("size()", is(8))
                    .body("subcategory_name",
                            hasItems(
                                    "buns",
                                    "patties",
                                    "cheeses",
                                    "vegetables",
                                    "addons",
                                    "sauces",
                                    "drinks",
                                    "sides"
                            ));
        }

        @Test
        @DisplayName("GET /api/subcategory should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            given()
                    .spec(authSpecInvalidManager())
            .when()
                    .get(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("GET /api/subcategory should return empty list when restaurant has no subcategories")
        void shouldReturnEmptyListForManagerWithoutSubcategories() {
            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                    .get(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(200)
                    .body("$", is(List.of()));
        }

        @Test
        @DisplayName("GET /api/subcategory should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            when()
                    .get(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(401);
        }

    }

    @DisplayName("POST /api/subcategory create subcategory")
    @Nested
    class createSubCategory {
        @Test
        @DisplayName("POST /api/subcategory should return 201 Created when user is MANAGER")
        void shouldCreateSubCategory() {
            String payload = "{\"subcategoryName\":\"test-subcategory\"}";

            Number idNumber = given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(payload)
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(201)
                    .body("subcategory_name", is("test-subcategory"))
                    .extract()
                    .path("id");

            subcategoryRepository.deleteById(idNumber.longValue());
        }

        @Test
        @DisplayName("POST /api/subcategory should return 403 Forbidden when user is EMPLOYEE")
        void shouldReturnForbiddenForEmployee() {
            String payload = "{\"subcategoryName\":\"test-subcategory\"}";

            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .body(payload)
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("POST /api/subcategory should return 400 Bad Request when request body is missing")
        void shouldReturnBadRequestWhenBodyMissing() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("POST /api/subcategory should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            String payload = "{\"subcategoryName\":\"test-subcategory\"}";

            given()
                    .body(payload)
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("POST /api/subcategory should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictWhenManagerHasNoRestaurant() {
            String payload = "{\"subcategoryName\":\"test-subcategory\"}";

            given()
                    .spec(authSpecInvalidManager())
                    .body(payload)
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("POST /api/subcategory should return 409 Conflict when creating duplicate subcategory")
        void shouldReturnConflictWhenCreatingDuplicateSubcategory() {
            String payload = "{\"subcategoryName\":\"test-subcategory\"}";

            Number idNumber = given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(payload)
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(201)
                    .extract()
                    .path("id");

            long countAfterFirst = subcategoryRepository.count();

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(payload)
            .when()
                    .post(SUB_CATEGORY_BASE_URL)
            .then()
                    .statusCode(409);

            long countAfterSecond = subcategoryRepository.count();
            Assertions.assertEquals(countAfterFirst, countAfterSecond, "Duplicate create should not persist a new subcategory");

            subcategoryRepository.deleteById(idNumber.longValue());
        }

    }

    @DisplayName("PATCH /api/subcategory/{id} patch subcategory")
    @Nested
    class patchSubCategory {
        @Test
        @DisplayName("PATCH /api/subcategory/{id} should return 200 OK when user is MANAGER")
        void shouldPatchSubCategory() {
            Long id = createSubcategoryForTest("patch-subcategory");
            String payload = "{\"subcategoryName\":\"patch-subcategory-updated\"}";

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(payload)
            .when()
                    .patch(SUB_CATEGORY_BASE_URL + "/{id}", id)
            .then()
                    .statusCode(200)
                    .body("subcategory_name", is("patch-subcategory-updated"));

            Subcategory subcategory = subcategoryRepository.findById(id).orElseThrow();
            Assertions.assertEquals("patch-subcategory-updated", subcategory.getSubcategoryName());

            subcategoryRepository.deleteById(id);
        }

        @Test
        @DisplayName("PATCH /api/subcategory/{id} should return 400 Bad Request when name is blank")
        void shouldReturnBadRequestForBlankName() {
            Long id = createSubcategoryForTest("patch-subcategory-bad");
            String payload = "{\"subcategoryName\":\"   \"}";

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(payload)
            .when()
                    .patch(SUB_CATEGORY_BASE_URL + "/{id}", id)
            .then()
                    .statusCode(400);

            subcategoryRepository.deleteById(id);
        }

        @Test
        @DisplayName("PATCH /api/subcategory/{id} should return 404 Not Found when subcategory does not exist")
        void shouldReturnNotFoundForMissingSubcategory() {
            String payload = "{\"subcategoryName\":\"missing-subcategory\"}";

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(payload)
            .when()
                    .patch(SUB_CATEGORY_BASE_URL + "/{id}", 99999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("PATCH /api/subcategory/{id} should return 403 Forbidden for different restaurant")
        void shouldReturnForbiddenForCrossTenantPatch() {
            String payload = "{\"subcategoryName\":\"cross-tenant-update\"}";
            Subcategory before = subcategoryRepository.findById(1L).orElseThrow();

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(payload)
            .when()
                    .patch(SUB_CATEGORY_BASE_URL + "/{id}", 1L)
            .then()
                    .statusCode(403);

            Subcategory after = subcategoryRepository.findById(1L).orElseThrow();
            Assertions.assertEquals(before.getSubcategoryName(), after.getSubcategoryName());
        }

        @Test
        @DisplayName("PATCH /api/subcategory/{id} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            String payload = "{\"subcategoryName\":\"unauthorized-update\"}";

            given()
                    .body(payload)
            .when()
                    .patch(SUB_CATEGORY_BASE_URL + "/{id}", 1L)
            .then()
                    .statusCode(401);
        }
    }

    @DisplayName("DELETE /api/subcategory/{id} delete subcategory")
    @Nested
    class deleteSubCategory {
        @Test
        @DisplayName("DELETE /api/subcategory/{id} should return 200 OK when user is MANAGER")
        void shouldDeleteSubCategory() {
            Long id = createSubcategoryForTest("delete-subcategory");

            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .delete(SUB_CATEGORY_BASE_URL + "/{id}", id)
            .then()
                    .statusCode(200)
                    .body("subcategory_name", is("delete-subcategory"));

            Assertions.assertTrue(subcategoryRepository.findById(id).isEmpty());
        }

        @Test
        @DisplayName("DELETE /api/subcategory/{id} should return 404 Not Found when subcategory does not exist")
        void shouldReturnNotFoundForMissingSubcategory() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
            .when()
                    .delete(SUB_CATEGORY_BASE_URL + "/{id}", 99999L)
            .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("DELETE /api/subcategory/{id} should return 403 Forbidden for different restaurant")
        void shouldReturnForbiddenForCrossTenantDelete() {
            Subcategory before = subcategoryRepository.findById(1L).orElseThrow();

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                    .delete(SUB_CATEGORY_BASE_URL + "/{id}", 1L)
            .then()
                    .statusCode(403);

            Subcategory after = subcategoryRepository.findById(1L).orElseThrow();
            Assertions.assertEquals(before.getSubcategoryName(), after.getSubcategoryName());
        }

        @Test
        @DisplayName("DELETE /api/subcategory/{id} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorizedWhenUserIsNotLoggedIn() {
            when()
                    .delete(SUB_CATEGORY_BASE_URL + "/{id}", 1L)
            .then()
                    .statusCode(401);
        }
    }

    private Long createSubcategoryForTest(String name) {
        String payload = "{\"subcategoryName\":\"" + name + "\"}";

        Number idNumber = given()
                .spec(authSpec(UserRole.MANAGER))
                .body(payload)
        .when()
                .post(SUB_CATEGORY_BASE_URL)
        .then()
                .statusCode(201)
                .extract()
                .path("id");

        return idNumber.longValue();
    }

}
