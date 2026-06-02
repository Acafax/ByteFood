package org.example;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.example.security.JwtService;
import org.example.security.SecurityUser;
import org.example.security.UserRole;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import java.security.SecureRandom;
import java.util.Base64;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"/clean.sql", "/db-init.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public abstract class IntegrationTestBase {

    @Autowired
    JwtService jwtService;

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("dbname")
            .withPassword("password")
            .withUsername("username")
            .withReuse(true);

    @Container
    @ServiceConnection
    protected static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:latest"));

    private static final String TEST_JWT_SECRET = generateTestJwtSecret();
    
    private static String generateTestJwtSecret() {
        byte[] keyBytes = new byte[32];
        new SecureRandom().nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
        registry.add("spring.security.jwt.secret-key", () -> TEST_JWT_SECRET);
    }

    @BeforeEach
    void setUpAssure(){
        RestAssured.baseURI="http://localhost";
        RestAssured.port=port;
    }

    @LocalServerPort
    protected int port;

    protected RequestSpecification authSpec(UserRole role){
        String token = switch (role){
            case ADMIN -> generateTestTokenADMIN();
            case MANAGER -> generateTestTokenMANEGER();
            case EMPLOYEE -> generateTestTokenEMPLOYEE();
        };

        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }

    private String generateTestTokenADMIN(){
        SecurityUser user = new SecurityUser(1L, "admin@email", "password", UserRole.ADMIN, "Admin", "Testowy", 1L);
        return jwtService.generateToken(user);
    }

    private String generateTestTokenMANEGER(){
        SecurityUser user = new SecurityUser(2L, "manager@email", "password", UserRole.MANAGER, "Manager", "Testowy", 1L);
        return jwtService.generateToken(user);
    }

    private String generateTestTokenEMPLOYEE(){
        SecurityUser user = new SecurityUser(3L, "employee@email", "password", UserRole.EMPLOYEE, "Employee", "Testowy", 1L);
        return jwtService.generateToken(user);
    }

    protected RequestSpecification authSpecInvalidManager(){
        SecurityUser user = new SecurityUser(4L, "invalidManager@email", "password", UserRole.MANAGER, "Manager", "Testowy", null);
        String token = jwtService.generateToken(user);

        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }

    protected RequestSpecification authSpecManagerRestaurantWithoutProducts(){
        SecurityUser user = new SecurityUser(5L, "managerEmptyRestaurant@email", "password", UserRole.MANAGER, "Manager", "Testowy", 2L);
        String token = jwtService.generateToken(user);

        return new RequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .setContentType(ContentType.JSON)
                .build();
    }


}

