package org.example.controllers;

import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.example.IntegrationTestBase;
import org.example.builders.ProductTestBuilder;
import org.example.dtos.CreateProductSemiProductsDto;
import org.example.models.Product;
import org.example.repositories.ProductRepository;
import org.example.repositories.ProductSemiProductRepository;
import org.example.repositories.projections.ProductProjection;
import org.example.security.UserRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;


@Slf4j
@DisplayName("Product Controller Test")
class ProductTest extends IntegrationTestBase {

    @Autowired
    EntityManager entityManager;

    private static final String PRODUCT_BASE_URL = "/api/products";

    private final ProductTestBuilder productTestBuilder = new ProductTestBuilder();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductSemiProductRepository productSemiProductRepository;

    @DisplayName("Test Manager without restaurant ID (Global Checks)")
    @Nested
    class ManagerWithoutRestaurantIdTest {

        @Test
        @DisplayName("GET /api/products/all-with-semi should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForGetAllProducts() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .get(PRODUCT_BASE_URL + "/all-with-semi")
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("GET /api/products/{productId} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForGetProductById() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .get(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("POST /api/products should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForCreateProduct() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createProductRequest.json").toPath());

            given()
                    .spec(authSpecInvalidManager())
                    .body(testProduct)
                    .when()
                    .post(PRODUCT_BASE_URL)
                    .then()
                    .statusCode(409);
        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 409 Conflict when manager has no restaurant")
        void shouldReturnConflictForDeleteProduct() {
            given()
                    .spec(authSpecInvalidManager())
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("Tests GET /products/{id} whould get product by id" )
    @Nested
    class getProductById{

        @Test
        @DisplayName("GET /api/products/{productId} should return 200 OK when product exists")
        void shouldGetProductById() {
            given()
                    .spec(authSpec( UserRole.EMPLOYEE))
                    .when()
                    .get(PRODUCT_BASE_URL+"/{productId}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Junior Classic Burger 120g"))
                    .body("category",  equalTo("BURGER"));
        }

        @Test
        @DisplayName("GET /api/products/{productId} should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequest(){
            given()
                    .spec(authSpec( UserRole.EMPLOYEE))
                    .when()
                    .get(PRODUCT_BASE_URL+"/{productId}", "numer")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("GET /api/products/{productId} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized(){

            when()
                    .get(PRODUCT_BASE_URL+"/{productId}", 1L)
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/products/{productId} should return 403 Forbidden when manager from different restaurant tries to access")
        void shouldReturnForbiddenForCrossTenantAccess() {
            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .when()
                    .get(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(403);
        }


        @Test
        @DisplayName("GET /api/products/{productId} should return 404 Not Found when product does not exist")
        void shouldReturnNotFound(){
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .when()
                    .get(PRODUCT_BASE_URL+"/{productId}", 1000L)
                    .then()
                    .statusCode(404);
        }



    }

    @DisplayName("Tests GET /products/{id}/with-semi whould get product by id" )
    @Nested
    class getProductByIdWithSemiProducts{

        @Test
        @DisplayName("GET /api/products/{productId}/with-semi should return 200 OK when product exists")
        void shouldGetProductById() {
            given().log().all()
                    .spec(authSpec( UserRole.EMPLOYEE))
                    .when().log().all()
                    .get(PRODUCT_BASE_URL+"/{productId}/with-semi", 1L)

                    .then().log().all()
                    .statusCode(200)
                    .body("name", equalTo("Junior Classic Burger 120g"))
                    .body("category",  equalTo("BURGER"));
        }

        @Test
        @DisplayName("GET /api/products/{productId}/with-semi should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequest(){
            given()
                    .spec(authSpec( UserRole.EMPLOYEE))
                    .when()
                    .get(PRODUCT_BASE_URL+"/{productId}/with-semi", "numer")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("GET /api/products/{productId}/with-semi should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized(){
            when()
                    .get(PRODUCT_BASE_URL+"/{productId}/with-semi", 1L)
                    .then()
                    .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/products/{productId}/with-semi should return 403 Forbidden when manager from different restaurant")
        void shouldReturnForbiddenIfDifferentRestaurantResourceWithSemi() {
            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .when()
                    .get(PRODUCT_BASE_URL + "/{productId}/with-semi", 1L)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("GET /api/products/{productId}/with-semi should return 404 Not Found when product does not exist")
        void shouldReturnNotFound(){
            given().log().all()
                    .spec(authSpec(UserRole.MANAGER))
                    .when().log().all()
                    .get(PRODUCT_BASE_URL+"/{productId}/with-semi", 1000L)
                    .then().log().all()
                    .statusCode(404);
        }
    }

    @DisplayName("Tests GET /products/all-with-semi to get all products for restaurant" )
    @Nested
    class getAllProductsForRestaurant {
        @Test
        @DisplayName("GET /api/products/all-with-semi should return 200 OK with product list when user is employee")
        void shouldGetAllProductsForRestaurant() {
            given()
                .spec(authSpec(UserRole.EMPLOYEE))
            .when()
                .get(PRODUCT_BASE_URL + "/all-with-semi")
            .then()
                .statusCode(200)
                .body("size()", is(21))
                .body("name", hasItems(
                        "Junior Classic Burger 120g",
                        "Classic Burger 180g",
                        "Goliath Burger 360g"
                ))
                .body("semiProducts.flatten().name", hasItems(
                        "Bułka brioche",
                        "Bułka ziemniaczana",
                        "Wołowina 180g"
                ));
        }

        @Test
        @DisplayName("GET /api/products/all-with-semi should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized() {
            when()
                .get(PRODUCT_BASE_URL + "/all-with-semi")
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("GET /api/products/all-with-semi should return 409 Conflict when manager has no restaurant")
        void shouldReturnBadRequest() {
            given()
                .spec(authSpecInvalidManager())
            .when()
                .get(PRODUCT_BASE_URL + "/all-with-semi")
            .then()
                .statusCode(409);
        }

        @Test
        @DisplayName("GET /api/products/all-with-semi should return 200 OK with empty list when restaurant has no products")
        void shouldReturnEmptyList() {
            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
            .when()
                .get(PRODUCT_BASE_URL + "/all-with-semi")
            .then()
                .statusCode(200)
                .body("size()", is(0));

        }

    }

    @DisplayName("Test POST /products to create product")
    @Nested
    class createProductTest {

        @Test
        @DisplayName("POST /api/products should return 201 Created when data is valid")
        void shouldCreateProduct() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createProductRequest.json").toPath());

            given()
                    .log().all()
                .spec(authSpec(UserRole.MANAGER))
                .body(testProduct)
            .when()                    .log().all()

                    .post(PRODUCT_BASE_URL)
            .then()                    .log().all()

                    .statusCode(201)
                .body("name", equalTo("testBurger"))
                .body("category", equalTo("BURGER"));
        }

        @Test
        @DisplayName("POST /api/products should return 400 Bad Request when validation fails")
        void shouldReturnBadRequestWhenInvalidData() throws IOException {
            String invalidProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createInvalidProductRequest.json").toPath());

            productTestBuilder
                    .withName("");

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(productTestBuilder.buildJson())
            .when()
                .post(PRODUCT_BASE_URL)
            .then()
                .statusCode(400);

            given()
                .config(RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL)))
                .spec(authSpec(UserRole.MANAGER))
                .body(invalidProduct)
            .when()
                .post(PRODUCT_BASE_URL)
            .then()
                .statusCode(400);

        }

        @Test
        @DisplayName("POST /api/products should return 403 Forbidden when user is not manager")
        void shouldReturnForbidden() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createProductRequest.json").toPath());

            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(testProduct)
            .when()
                .post(PRODUCT_BASE_URL)
            .then()
                .statusCode(403);

        }

        @Test
        @DisplayName("POST /api/products should return 404 Not Found when semi product does not exist")
        void shouldReturnBadRequestWhenSemiProductNotExist() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createProductWithNotExistSemiProductRequest.json").toPath());

            given()                    .log().all()

                    .spec(authSpec(UserRole.MANAGER))
                .body(testProduct)
            .when()                    .log().all()

                    .post(PRODUCT_BASE_URL)
            .then()                    .log().all()

                    .statusCode(404);
        }

        @Test
        @DisplayName("POST /api/products should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized() {
            when()
                .post(PRODUCT_BASE_URL + "/create")
            .then()
                .statusCode(401);

        }

        @Test
        @DisplayName("POST /api/products should return 403 Forbidden when creating product with semiProductId from another restaurant (cross-tenant injection)")
        void shouldReturnForbiddenWhenUsingSemiProductFromAnotherRestaurant() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createProductWithForeignSemiProduct.json").toPath());

            long countBefore = productRepository.count();

            given()
                .spec(authSpecManagerRestaurantWithoutProducts())
                .body(testProduct)
            .when()
                .post(PRODUCT_BASE_URL)
            .then()
                .statusCode(403);

            // Verification of the absence of side effects in DB
            long countAfter = productRepository.count();
            Assertions.assertEquals(countBefore, countAfter, "No new product should be created in DB after cross-tenant injection attempt");
        }

        @Test
        @DisplayName("POST /api/products should return 403 and NOT create product in DB when user is EMPLOYEE")
        void shouldReturnForbiddenAndNotCreateProductWhenEmployee() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/createProductRequest.json").toPath());

            long countBefore = productRepository.count();

            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(testProduct)
            .when()
                .post(PRODUCT_BASE_URL)
            .then()
                .statusCode(403);

            // Verification of the absence of side effects in DB
            long countAfter = productRepository.count();
            Assertions.assertEquals(countBefore, countAfter, "No new product should be created in DB after EMPLOYEE attempt");
        }
    }


    @DisplayName("Test PATCH /products/{id} to patch product")
    @Nested
    class patchProductTest{
        @Test
        @DisplayName("PATCH /api/products/{Id} should return 200 OK when product successful patched")
        void shouldPatchProduct() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/patchProductNameAndCategory.json").toPath());

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(testProduct)
            .when()
                    .patch(PRODUCT_BASE_URL+"/{Id}", 1L)
            .then()
                .statusCode(200)
                .body("name", is("NEW NAME"))
                .body("category", is("differentCategory"));

            ProductProjection product = productRepository.findProductById(1L);
            Assertions.assertEquals("NEW NAME", product.getName());
            Assertions.assertEquals("differentCategory", product.getCategory());

        }

        // // Product ID: 1 has 6 semiProducts; after the patch, it should have 3 semiProducts.
        @Test
        @DisplayName("PATCH /api/products/{Id} should return 200 OK when product semi-prods successful patched")
        void shouldPatchProductWithSemiProducts() throws IOException{
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/patchProductNameAndComponents.json").toPath());

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(testProduct)
                    .when()
                    .patch(PRODUCT_BASE_URL+"/{Id}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", is("NEW NAME"));

            Product product = productRepository.findWithSemiProductsById(1L).orElseThrow(EntityNotFoundException::new);
            Assertions.assertEquals("NEW NAME", product.getName());
            Assertions.assertEquals(3, product.getSemiProducts().size());

        }

        @Test
        @DisplayName("PATCH /api/products/{Id} should return 400 BAD_REQUEST when manager try update to invalid variables ")
        void shouldBadRequest(){
            var testRequest = productTestBuilder.withPrice(new BigDecimal("-10")).buildMap();

            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .body(testRequest)
                    .when()
                    .patch(PRODUCT_BASE_URL+"/{Id}", 1L)
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("PATCH /api/products/{Id} should return 401 UNAUTHORIZED when user is NOT logged in")
        void shouldReturnUnauthorized() {
            var patchProduct = productTestBuilder.buildMap();

            given()
                .body(patchProduct)
            .when()
                .patch(PRODUCT_BASE_URL+"/{Id}", 1L)
            .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("PATCH /api/products/{Id} should return 403 FORBIDDEN when user is NOT manager")
        void shouldReturnForbidden()throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/patchProductNameAndCategory.json").toPath());

            given()
                .spec(authSpec(UserRole.EMPLOYEE))
                .body(testProduct)
            .when()
                .patch(PRODUCT_BASE_URL+"/{Id}", 1L)
            .then()
                .statusCode(403);
        }

        @Test
        @DisplayName("PATCH /api/products/{Id} should return 403 Forbidden when manager from different restaurant tries to patch")
        void shouldReturnForbiddenForCrossTenantPatch() throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/patchProductNameAndCategory.json").toPath());

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(testProduct)
                    .when()
                    .patch(PRODUCT_BASE_URL + "/{Id}", 1L)
                    .then()
                    .statusCode(403);

//            check that product didn't change
            ProductProjection productById = productRepository.findProductById(1L);
            Assertions.assertNotEquals("differentCategory", productById.getCategory());
            Assertions.assertNotEquals("NEW NAME", productById.getName());


        }


        @Test
        @DisplayName("PATCH /api/products/{Id} should return 403 Forbidden when patching product with semiProductId from another restaurant (cross-tenant injection)")
        void shouldReturnForbiddenForCrossTenantSemiProductInjection() throws IOException {
            String patchWithForeignSemi = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/patchProductNameAndComponents.json").toPath());

            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .body(patchWithForeignSemi)
                    .when()
                    .patch(PRODUCT_BASE_URL + "/{Id}", 1L)
                    .then()
                    .statusCode(403);

            // Verification of the absence of side effects in DB
            Product product = productRepository.findWithSemiProductsById(1L).orElseThrow(EntityNotFoundException::new);
            Assertions.assertNotEquals("NEW NAME", product.getName(), "Product name should not be changed after cross-tenant injection attempt");
        }

        @Test
        @DisplayName("PATCH /api/products/{Id} should return 404 NOT_FOUND when product NOT exist")
        void shouldReturnNotFound()throws IOException {
            String testProduct = Files.readString(ResourceUtils.getFile("classpath:productControllerTestCases/patchProductNameAndCategory.json").toPath());

            given()
                .spec(authSpec(UserRole.MANAGER))
                .body(testProduct)
            .when()
                .patch(PRODUCT_BASE_URL+"/{Id}", 999L)
            .then()
                .statusCode(404);
        }

    }

    @DisplayName("Test DELETE /products/{id} to delete product")
    @Nested
    class deleteProductTest {

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 200 OK when product exists")
        void shouldDeleteProductById() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Junior Classic Burger 120g"))
                    .body("category", equalTo("BURGER"));

            boolean deletedProduct = productRepository.existsById(1L);
            Assertions.assertEquals(false, deletedProduct, "Product should be soft deleted");

            boolean deletedProductRelation = productSemiProductRepository.existsById(1L);
            Assertions.assertEquals( false, deletedProductRelation ,"Cascade delete of product semi products relation should work");
        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should soft delete product when product exists")
        void deletedProductShouldBySoftDeleted() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 2L)
                    .then()
                    .statusCode(200)
                    .body("name", equalTo("Classic Burger 180g"))
                    .body("category", equalTo("BURGER"));

            Boolean recordExistsInDb = (Boolean) entityManager.createNativeQuery(
                            "SELECT EXISTS (SELECT 1 FROM products WHERE id = :id AND deleted_at IS NOT NULL)")
                    .setParameter("id", 2L)
                    .getSingleResult();

            boolean deletedProduct = productRepository.existsById(2L);

            // 1. Record still exist in db (SQL)
            Assertions.assertTrue( recordExistsInDb, "Product should exist in DB with deleted=true");

            // 2. Record 'dont exit' (soft deleted) in db (JPA)
            Assertions.assertFalse(deletedProduct, "Repository should not find soft deleted product");
        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 400 Bad Request when id is invalid")
        void shouldReturnBadRequest() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", "numer")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 401 Unauthorized when user is not logged in")
        void shouldReturnUnauthorized() {
            when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(401);

        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 403 Forbidden when user is employee")
        void shouldReturnForbidden() {
            given()
                    .spec(authSpec(UserRole.EMPLOYEE))
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(403);

        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 403 Forbidden when manager from different restaurant tries to delete")
        void shouldReturnForbiddenForCrossTenantDelete() {
            given()
                    .spec(authSpecManagerRestaurantWithoutProducts())
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 1L)
                    .then()
                    .statusCode(403);

            // Verification of the absence of side effects in DB
            Optional<Product> productById = productRepository.findById(1L);
            Assertions.assertTrue(productById.isPresent());
        }

        @Test
        @DisplayName("DELETE /api/products/{productId} should return 404 Not Found when product does not exist")
        void shouldReturnNotFound() {
            given()
                    .spec(authSpec(UserRole.MANAGER))
                    .when()
                    .delete(PRODUCT_BASE_URL + "/{productId}", 1000L)
                    .then()
                    .statusCode(404);
        }
    }


}