package org.example.posFX.apiCommunication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.posFX.auth.device.DeviceApiKeyResetRequest;
import org.example.posFX.auth.device.DeviceApiKeyResponse;
import org.example.posFX.auth.device.DeviceAuthPayload;
import org.example.posFX.session.AuthSession;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

public class ApiClient {

    private final HttpClient httpClient;
    private final AuthSession authSession;
    private final ObjectMapper objectMapper;
    private final String apiBaseUrl;

    public ApiClient(HttpClient httpClient, AuthSession authSession, ObjectMapper objectMapper, String apiBaseUrl) {
        this.httpClient = Objects.requireNonNull(httpClient);
        this.authSession = Objects.requireNonNull(authSession);
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl);
    }

    public Optional<String> generateApiKey(DeviceAuthPayload payload) {
        return requestApiKey(ApiEndpoints.GENERATE_API_KEY, payload);
    }

    public boolean deleteDeviceApiKeyOnServer(String apiKey) {
        Optional<String> accessToken = authSession.getAccessToken();
        if (accessToken.isEmpty() || apiKey == null || apiKey.isBlank()) {
            return false;
        }

        try {
            String json = objectMapper.writeValueAsString(new DeviceApiKeyResetRequest(apiKey.trim()));
            HttpRequest request = HttpRequest.newBuilder(ApiEndpoints.DELETE_DEVICE_API_KEY.toUri(apiBaseUrl))
                    .header("Authorization", "Bearer " + accessToken.get())
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            int status = response.statusCode();
            if (status == 200 || status == 204) {
                return true;
            }

            if (status == 401 || status == 403) {
                return false;
            }
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return false;
    }

    private Optional<String> requestApiKey(ApiEndpoints endpoint, DeviceAuthPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(endpoint.toUri(apiBaseUrl))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8));

            authSession.getAccessToken().ifPresent(token ->
                    requestBuilder.header("Authorization", "Bearer " + token)
            );

            HttpResponse<String> response = httpClient.send(
                    requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            int status = response.statusCode();
            if (status == 200 || status == 201) {
                return parseApiKey(response.body());
            }

            if (status == 401 || status == 403) {
                return Optional.empty();
            }
        } catch (IOException e) {
            return Optional.empty();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Optional.empty();
    }

    private Optional<String> parseApiKey(String body) throws IOException {
        if (body == null || body.isBlank()) {
            return Optional.empty();
        }

        String trimmed = body.trim();
        if (trimmed.startsWith("{")) {
            DeviceApiKeyResponse response = objectMapper.readValue(trimmed, DeviceApiKeyResponse.class);
            if (response.apiKey() != null && !response.apiKey().isBlank()) {
                return Optional.of(response.apiKey().trim());
            }
            return Optional.empty();
        }

        return Optional.of(trimmed);
    }
}
