package org.example.posFX.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.posFX.apiCommunication.ApiEndpoints;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class AuthApiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiBaseUrl;

    public AuthApiService(HttpClient httpClient, ObjectMapper objectMapper, String apiBaseUrl) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl);
    }

    public CompletableFuture<LoginResult> loginAsync(String email, String password) {
        return CompletableFuture.supplyAsync(() -> loginBlocking(email, password));
    }

    private LoginResult loginBlocking(String email, String password) {
        try {
            String json = objectMapper.writeValueAsString(new LoginCredentialsPayload(email, password));
            HttpRequest request = HttpRequest.newBuilder(ApiEndpoints.AUTH_LOGIN.toUri(apiBaseUrl))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 200) {
                LoginTokenResponse dto = objectMapper.readValue(response.body(), LoginTokenResponse.class);
                if (dto.token() == null || dto.token().isBlank()) {
                    return LoginResult.fail("Brak tokena w odpowiedzi serwera.");
                }
                return LoginResult.ok(dto.token());
            }
            if (response.statusCode() == 401 || response.statusCode() == 403) {
                return LoginResult.fail("Nieprawidłowy email lub hasło.");
            }
            return LoginResult.fail("Serwis niedostępny. Spróbuj ponownie.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return LoginResult.fail("Logowanie przerwane.");
        } catch (Exception e) {
            return LoginResult.fail("Błąd połączenia z serwerem.");
        }
    }
}
