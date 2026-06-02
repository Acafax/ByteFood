package org.example.posFX.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.example.posFX.apiCommunication.ApiClient;
import org.example.posFX.navigation.SceneNavigator;
import org.example.posFX.session.AuthSession;

import java.net.http.HttpClient;

public final class LoginPageController {

    private final AuthApiService authApiService;
    private final AuthSession authSession;
    private final SceneNavigator sceneNavigator;
    private final ObjectMapper objectMapper;
    private final ApiClient apiClient;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator progressIndicator;

    public LoginPageController(AuthApiService authApiService, AuthSession authSession, SceneNavigator sceneNavigator, ObjectMapper objectMapper, ApiClient apiClient) {
        this.authApiService = authApiService;
        this.authSession = authSession;
        this.sceneNavigator = sceneNavigator;
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    @FXML
    private void initialize() {
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
        progressIndicator.setVisible(false);
        progressIndicator.setManaged(false);
        Platform.runLater(() -> emailField.requestFocus());
    }

    @FXML
    private void onLoginClicked() {
        errorLabel.setText("");
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);

        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";

        if (email.isEmpty() || password.isEmpty()) {
            showError("Wpisz email i hasło.");
            return;
        }

        setBusy(true);

        authApiService.loginAsync(email, password).whenComplete((result, throwable) -> Platform.runLater(() -> {
            setBusy(false);
            if (throwable != null) {
                showError("Logowanie nie powiodło się.");
                return;
            }
            if (result.success()) {
                authSession.setAccessToken(result.accessToken());
                passwordField.clear();
                sceneNavigator.showPos();
            } else {
                showError(result.errorMessage() != null ? result.errorMessage() : "Logowanie nie powiodło się.");
            }
        }));
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
        emailField.setDisable(busy);
        passwordField.setDisable(busy);
        progressIndicator.setVisible(busy);
        progressIndicator.setManaged(busy);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }
}
