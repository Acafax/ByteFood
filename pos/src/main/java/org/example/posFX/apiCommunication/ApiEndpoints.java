package org.example.posFX.apiCommunication;

import java.net.URI;

public enum ApiEndpoints {
    AUTH_LOGIN("/api/auth/login"),
    GENERATE_API_KEY("/api/device-api-key/generate"),
    DELETE_DEVICE_API_KEY("/api/device-api-key/reset"),
    CREATE_ORDER("/api/orders/");

    private final String path;

    ApiEndpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public URI toUri(String apiBaseUrl) {
        String base = apiBaseUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return URI.create(base + path);
    }
}
