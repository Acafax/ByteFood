package org.example.posFX.auth.device;

import oshi.SystemInfo;

import java.util.Optional;

public class CredentialService {

    private final SystemCredentials systemCredentials;

    public CredentialService() {
        SystemInfo systemInfo = new SystemInfo();
        String family = systemInfo.getOperatingSystem().getFamily();

        if (family.contains("Linux")) {
            this.systemCredentials = new LinuxCredentials();
        } else if (family.contains("Windows")) {
            this.systemCredentials = new WindowsCredentials();
        } else {
            throw new UnsupportedOperationException("Nieobsługiwany system operacyjny: " + family);
        }
    }

    CredentialService(SystemCredentials systemCredentials) {
        this.systemCredentials = systemCredentials;
    }

    public void setApiKey(String apiKey) {
        systemCredentials.setCredentials(apiKey);
    }

    public Optional<String> getApiKey() {
        return systemCredentials.getCredentials();
    }

    public void deleteApiKey() {
        systemCredentials.deleteCredentials();
    }
}
