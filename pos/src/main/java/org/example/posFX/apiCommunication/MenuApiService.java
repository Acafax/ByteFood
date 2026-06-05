package org.example.posFX.apiCommunication;

import javafx.application.Platform;
import org.example.posFX.auth.device.CredentialService;
import org.example.posFX.objects.OrderItem;
import org.example.posFX.session.AuthorizedHttpRequestFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MenuApiService {

    private static final Logger LOG = Logger.getLogger(MenuApiService.class.getName());

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final AuthorizedHttpRequestFactory requestFactory;
    private final OrderMapper orderMapper;
    private final String apiBaseUrl;
    private final CredentialService credentialService;

    public MenuApiService(
            AuthorizedHttpRequestFactory requestFactory,
            OrderMapper orderMapper,
            String apiBaseUrl, CredentialService credentialService
    ) {
        this.requestFactory = Objects.requireNonNull(requestFactory);
        this.orderMapper = Objects.requireNonNull(orderMapper);
        this.apiBaseUrl = Objects.requireNonNull(apiBaseUrl);
        this.credentialService = credentialService;
    }

    public void submitOrderAsync(List<OrderItem> orderItems, Consumer<String> onSuccess, Consumer<String> onError) {
        if (orderItems == null || orderItems.isEmpty()) {
            Platform.runLater(() -> onError.accept("Koszyk jest pusty."));
            return;
        }

        String apiKey = credentialService.getApiKey()
                .orElseThrow(() -> new IllegalStateException("Brak ApiKey do autoryzacji żądania."));

        try {
            String json = orderMapper.toCreateOrderJson(orderItems);
            HttpRequest request = requestFactory
                    .newBuilder(ApiEndpoints.CREATE_ORDER.toUri(apiBaseUrl))
                    .header("X-API-KEY", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        int statusCode = response.statusCode();
                        if (statusCode >= 200 && statusCode < 300) {
                            Platform.runLater(() -> onSuccess.accept("Zamówienie zostało złożone."));
                        } else {
                            LOG.log(Level.WARNING, "Order submission failed with status {0}", statusCode);
                            Platform.runLater(() -> onError.accept(
                                    "Serwer zwrócił błąd: " + statusCode
                            ));
                        }
                    })
                    .exceptionally(e -> {
                        LOG.log(Level.WARNING, "Order submission failed", e);
                        Platform.runLater(() -> onError.accept(
                                "Błąd połączenia: " + e.getMessage()
                        ));
                        return null;
                    });
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to prepare order request", e);
            Platform.runLater(() -> onError.accept(
                    "Błąd wewnętrzny aplikacji podczas przygotowywania żądania."
            ));
        }
    }
}
