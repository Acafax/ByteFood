package org.example.posFX.auth.device;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.example.posFX.navigation.SceneNavigator;

public class DeviceAuthController {

    private final SceneNavigator sceneNavigator;
    private final DeviceAuthService deviceAuthService;

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField posNameField;
    @FXML private TextField restaurantNameField;
    @FXML private Button authenticateButton;
    @FXML private Label errorLabel;
    @FXML private ProgressIndicator progressIndicator;

    public DeviceAuthController(SceneNavigator sceneNavigator, DeviceAuthService deviceAuthService) {
        this.sceneNavigator = sceneNavigator;
        this.deviceAuthService = deviceAuthService;
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
    private void onAuthenticateClicked() {
        clearError();

        DeviceAuthPayload payload = buildPayload();
        if (!validate(payload)) {
            return;
        }

        setBusy(true);

        deviceAuthService.registerDeviceAsync(payload).whenComplete((ignored, throwable) -> Platform.runLater(() -> {
            setBusy(false);

            if (throwable != null) {
                showError(resolveErrorMessage(throwable));
                return;
            }

            passwordField.clear();
            sceneNavigator.showLogin();
        }));
    }

    private DeviceAuthPayload buildPayload() {
        return new DeviceAuthPayload(
                trimToNull(emailField.getText()),
                passwordField.getText() != null ? passwordField.getText() : "",
                trimToNull(posNameField.getText()),
                trimToNull(restaurantNameField.getText())
        );
    }

    private boolean validate(DeviceAuthPayload payload) {
        if (payload.email() == null || payload.email().isBlank()) {
            showError("Wpisz email.");
            return false;
        }
        if (payload.password().isBlank()) {
            showError("Wpisz hasło.");
            return false;
        }
        if (payload.posName() == null || payload.posName().isBlank()) {
            showError("Wpisz nazwę POS.");
            return false;
        }
        if (payload.restaurantName() == null || payload.restaurantName().isBlank()) {
            showError("Wpisz nazwę restauracji.");
            return false;
        }
        return true;
    }

    private String resolveErrorMessage(Throwable throwable) {
        Throwable cause = throwable.getCause() != null ? throwable.getCause() : throwable;
        if (cause instanceof CredentialStorageException credentialStorageException) {
            return credentialStorageException.getMessage();
        }
        return "Operacja nie powiodła się. Spróbuj ponownie.";
    }

    private void setBusy(boolean busy) {
        authenticateButton.setDisable(busy);
        emailField.setDisable(busy);
        passwordField.setDisable(busy);
        posNameField.setDisable(busy);
        restaurantNameField.setDisable(busy);
        progressIndicator.setVisible(busy);
        progressIndicator.setManaged(busy);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setManaged(false);
        errorLabel.setVisible(false);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setManaged(true);
        errorLabel.setVisible(true);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
