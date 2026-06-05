package org.example.posFX;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Application;
import javafx.stage.Stage;
import org.example.posFX.apiCommunication.ApiClient;
import org.example.posFX.apiCommunication.MenuApiService;
import org.example.posFX.apiCommunication.OrderMapper;
import org.example.posFX.auth.AuthApiService;
import org.example.posFX.auth.device.CredentialService;
import org.example.posFX.auth.device.DeviceAuthService;
import org.example.posFX.navigation.SceneNavigator;
import org.example.posFX.session.AuthSession;
import org.example.posFX.session.AuthorizedHttpRequestFactory;

import java.net.http.HttpClient;
import java.util.Optional;

public class HelloApplication extends Application {

    private static final String DEFAULT_API_BASE = "https://bytefood.pl";

    @Override
    public void start(Stage primaryStage) {
        String apiBase = resolveApiBaseUrl();

        CredentialService credentialService = new CredentialService();
        AuthSession authSession = new AuthSession();
        AuthorizedHttpRequestFactory requestFactory = new AuthorizedHttpRequestFactory(authSession);
        ObjectMapper objectMapper = createObjectMapper();
        HttpClient httpClient = HttpClient.newHttpClient();
        AuthApiService authApiService = new AuthApiService(httpClient, objectMapper, apiBase);
        ApiClient apiClient = new ApiClient(httpClient, authSession, objectMapper, apiBase);
        OrderMapper orderMapper = new OrderMapper(objectMapper);
        MenuApiService menuApiService = new MenuApiService(requestFactory, orderMapper, apiBase, credentialService);
        DeviceAuthService deviceAuthService = new DeviceAuthService(apiClient, authSession, credentialService);

        String stylesheet = stylesheetUrl();
        SceneNavigator navigator = new SceneNavigator(
                primaryStage,
                1200,
                700,
                stylesheet,
                authSession,
                requestFactory,
                authApiService,
                httpClient,
                objectMapper,
                apiClient,
                menuApiService,
                deviceAuthService,
                credentialService
        );

        Optional<String> apiKey = credentialService.getApiKey();
        if (apiKey.isEmpty()) {
            primaryStage.setTitle("ByteFood - Device Authentication");
            navigator.showDeviceAuthentifier();
            primaryStage.show();
            return;
        }

        primaryStage.setTitle("ByteFood - System Zamówień");
        navigator.showLogin();
        primaryStage.show();
    }

    private static String resolveApiBaseUrl() {
        String fromEnv = System.getenv("BYTEFOOD_API_URL");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim().replaceAll("/$", "");
        }
        return DEFAULT_API_BASE;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private static String stylesheetUrl() {
        var url = HelloApplication.class.getResource("style.css");
        return url != null ? url.toExternalForm() : null;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
