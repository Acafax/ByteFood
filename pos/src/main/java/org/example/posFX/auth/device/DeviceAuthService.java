package org.example.posFX.auth.device;

import org.example.posFX.apiCommunication.ApiClient;
import org.example.posFX.session.AuthSession;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DeviceAuthService {

    private final ApiClient apiClient;
    private final AuthSession authSession;
    private final CredentialService credentialService;

    public DeviceAuthService(
            ApiClient apiClient,
            AuthSession authSession,
            CredentialService credentialService
    ) {
        this.apiClient = apiClient;
        this.authSession = authSession;
        this.credentialService = credentialService;
    }

    public CompletableFuture<Void> registerDeviceAsync(DeviceAuthPayload payload) {
        return CompletableFuture.runAsync(() -> registerDevice(payload));
    }

    public CompletableFuture<Void> resetDeviceApiKeyAsync() {
        return CompletableFuture.runAsync(this::resetDeviceApiKey);
    }

    public void registerDevice(DeviceAuthPayload payload) {
        Optional<String> apiKey = apiClient.generateApiKey(payload);
        persistApiKey(apiKey, "Nie udało się wygenerować ApiKey urządzenia.");
    }

    public void resetDeviceApiKey() {
        if (authSession.getAccessToken().isEmpty()) {
            throw new CredentialStorageException("Brak aktywnej sesji do resetu ApiKey.");
        }
        String apiKey = credentialService.getApiKey()
                .orElseThrow(() -> new CredentialStorageException("Brak ApiKey do usunięcia z serwera."));
        if (!apiClient.deleteDeviceApiKeyOnServer(apiKey)) {
            throw new CredentialStorageException("Nie udało się usunąć ApiKey z serwera.");
        }
        credentialService.deleteApiKey();
        authSession.clear();
    }

    private void persistApiKey(Optional<String> apiKey, String failureMessage) {
        if (apiKey.isEmpty()) {
            throw new CredentialStorageException(failureMessage);
        }

        credentialService.setApiKey(apiKey.get());
        authSession.clear();
    }
}
