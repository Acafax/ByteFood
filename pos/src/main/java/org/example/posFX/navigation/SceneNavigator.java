package org.example.posFX.navigation;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.posFX.HelloApplication;
import org.example.posFX.ProductController;
import org.example.posFX.apiCommunication.ApiClient;
import org.example.posFX.apiCommunication.MenuApiService;
import org.example.posFX.auth.AuthApiService;
import org.example.posFX.auth.LoginPageController;
import org.example.posFX.auth.device.CredentialService;
import org.example.posFX.auth.device.DeviceAuthController;
import org.example.posFX.auth.device.DeviceAuthService;
import org.example.posFX.session.AuthSession;
import org.example.posFX.session.AuthorizedHttpRequestFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.util.Objects;

/**
 * Zarządza przełączaniem scen na jednej {@link Stage} (logowanie ↔ POS).
 */
public final class SceneNavigator {

    private final Stage stage;
    private final double sceneWidth;
    private final double sceneHeight;
    private final String stylesheetUrl;
    private final AuthSession authSession;
    private final AuthorizedHttpRequestFactory requestFactory;
    private final AuthApiService authApiService;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ApiClient apiClient;
    private final MenuApiService menuApiService;
    private final DeviceAuthService deviceAuthService;
    private final CredentialService credentialService;

    public SceneNavigator(
            Stage stage,
            double sceneWidth,
            double sceneHeight,
            String stylesheetUrl,
            AuthSession authSession,
            AuthorizedHttpRequestFactory requestFactory,
            AuthApiService authApiService,
            HttpClient httpClient,
            ObjectMapper objectMapper,
            ApiClient apiClient,
            MenuApiService menuApiService,
            DeviceAuthService deviceAuthService,
            CredentialService credentialService
    ) {
        this.stage = Objects.requireNonNull(stage);
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
        this.stylesheetUrl = stylesheetUrl;
        this.authSession = authSession;
        this.requestFactory = requestFactory;
        this.authApiService = authApiService;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.apiClient = apiClient;
        this.menuApiService = menuApiService;
        this.deviceAuthService = deviceAuthService;
        this.credentialService = credentialService;
    }

    public void showDeviceAuthentifier() {
        authSession.clear();

        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("device-authentifier.fxml"));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();
            applyScene(root, "ByteFood - Rejestracja urządzenia");
        } catch (IOException e) {
            throw new UncheckedIOException("Nie można załadować widoku DeviceAuthentifier", e);
        }
    }

    public void showLogin() {
        authSession.clear();
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login-page.fxml"));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();
            applyScene(root, "ByteFood - System Zamówień");
        } catch (IOException e) {
            throw new UncheckedIOException("Nie można załadować widoku logowania", e);
        }
    }

    public void showPos() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
            loader.setControllerFactory(this::createController);
            Parent root = loader.load();
            applyScene(root, "ByteFood - System Zamówień");
        } catch (IOException e) {
            throw new UncheckedIOException("Nie można załadować widoku POS", e);
        }
    }

    private void applyScene(Parent root, String title) {
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        if (stylesheetUrl != null) {
            scene.getStylesheets().add(stylesheetUrl);
        }
        stage.setTitle(title);
        stage.setScene(scene);
    }

    private Object createController(Class<?> type) {
        if (type == LoginPageController.class) {
            return new LoginPageController(authApiService, authSession, this, objectMapper, apiClient);
        }
        if (type == ProductController.class) {
            return new ProductController(
                    authSession,
                    this,
                    objectMapper,
                    apiClient,
                    menuApiService,
                    deviceAuthService
            );
        }
        if (type == DeviceAuthController.class) {
            return new DeviceAuthController(this, deviceAuthService);
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Brak domyślnego konstruktora dla kontrolera: " + type.getName(), e);
        }
    }
}
